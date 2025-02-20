package com.sts.demo.configuration

import com.sts.demo.configuration.oauth2.OAuth2AccessTokenResponseConverterWithDefaults
import com.sts.demo.configuration.oauth2.OAuth2LoginFailureHandler
import com.sts.demo.configuration.oauth2.OAuth2LoginSuccessHandler
import com.sts.demo.security.JwtRequestFilter
import com.sts.demo.service.auth.CustomOAuth2UserService
import com.sts.demo.service.auth.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.client.RestTemplate
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableMethodSecurity
class SecurityConfig(
	private val customUserDetailsService: CustomUserDetailsService,
	private val customOAuth2UserService: CustomOAuth2UserService,
	private val oAuth2LoginSuccessHandler: OAuth2LoginSuccessHandler,
	private val oAuth2LoginFailureHandler: OAuth2LoginFailureHandler,
	private val jwtRequestFilter: JwtRequestFilter
) {

	@Bean
	@Order(1)
	fun apiSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
		http
			.securityMatcher("/api/**")
			.csrf { it.disable() }
			.authorizeHttpRequests { auth ->
				auth.requestMatchers("/api/auth/login").permitAll()
					.requestMatchers("/api/public/**").permitAll()
					.anyRequest().authenticated()
			}
			.sessionManagement { session ->
				session.sessionCreationPolicy(SessionCreationPolicy.NEVER)
			}
			.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)

		return http.build()
	}

	@Bean
	@Order(2)
	fun webSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
		http
			.csrf { csrf ->
				csrf.ignoringRequestMatchers("/api/auth/**")
			}
			.authorizeHttpRequests {
				it.requestMatchers(
					"/",
					"/login",
					"/error",
					"/oauth2/**",
					"/v3/api-docs",
					"/swagger-ui/**",
					"/swagger-ui.html",
					"/hello"
				).permitAll()
					.requestMatchers("/users").hasAnyRole("ADMIN", "SUPPORTER")
					.anyRequest().authenticated()
			}
			.formLogin { login ->
				login
					.loginPage("/login")
					.defaultSuccessUrl("/dashboard", true)
					.failureUrl("/login?error=true")
					.permitAll()
			}
			.userDetailsService(customUserDetailsService)
			.oauth2Login { oauth2 ->
				oauth2
					.loginPage("/login")
					.userInfoEndpoint { it.userService(customOAuth2UserService) }
					.tokenEndpoint { token -> token.accessTokenResponseClient(customOAuth2AccessTokenResponseClient()) }
					.successHandler(oAuth2LoginSuccessHandler)
					.failureHandler(oAuth2LoginFailureHandler)
			}
			.logout { logout ->
				logout
					.logoutUrl("/perform-logout")
					.logoutSuccessUrl("/")
					.invalidateHttpSession(true)
					.deleteCookies("JSESSIONID")
					.permitAll()
			}

		return http.build()
	}

	fun customOAuth2AccessTokenResponseClient(): OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
		val tokenResponseHttpMessageConverter = OAuth2AccessTokenResponseHttpMessageConverter()
		tokenResponseHttpMessageConverter.setAccessTokenResponseConverter(OAuth2AccessTokenResponseConverterWithDefaults())

		val restTemplate = RestTemplate(
			listOf(
				FormHttpMessageConverter(), tokenResponseHttpMessageConverter
			)
		)
		restTemplate.errorHandler = OAuth2ErrorResponseErrorHandler()

		val tokenResponseClient = DefaultAuthorizationCodeTokenResponseClient()
		tokenResponseClient.setRestOperations(restTemplate)

		return tokenResponseClient
	}

	@Bean
	fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
		return authenticationConfiguration.authenticationManager
	}

	@Bean
	fun corsConfigurationSource(): CorsConfigurationSource {
		val configuration = CorsConfiguration()
		configuration.allowedOrigins = listOf("http://localhost:3000") // Your frontend URL
		configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
		configuration.allowedHeaders = listOf("*")
		configuration.allowCredentials = true
		val source = UrlBasedCorsConfigurationSource()
		source.registerCorsConfiguration("/**", configuration)
		return source
	}
}
