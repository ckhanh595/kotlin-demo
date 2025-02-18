package com.sts.demo.configuration

import com.sts.demo.configuration.oauth2.OAuth2AccessTokenResponseConverterWithDefaults
import com.sts.demo.configuration.oauth2.OAuth2LoginFailureHandler
import com.sts.demo.configuration.oauth2.OAuth2LoginSuccessHandler
import com.sts.demo.service.CustomOAuth2UserService
import com.sts.demo.service.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.client.RestTemplate

@Configuration
class SecurityConfig(
	private val customUserDetailsService: CustomUserDetailsService,
	private val customOAuth2UserService: CustomOAuth2UserService,
	private val oAuth2LoginSuccessHandler: OAuth2LoginSuccessHandler,
	private val oAuth2LoginFailureHandler: OAuth2LoginFailureHandler,
) {

	@Bean
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
		http
			.authorizeHttpRequests {
				it.requestMatchers("/", "/login", "/error", "/oauth2/**").permitAll()
					.requestMatchers("/users").hasAnyRole("ADMIN", "SUPPORTER")
					.anyRequest().authenticated()
			}
			.formLogin { login ->
				login
					.loginPage("/login")
					.defaultSuccessUrl("/dashboard", true)
					.permitAll()
			}
			.userDetailsService(customUserDetailsService)
			.oauth2Client(Customizer.withDefaults())
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

		val restTemplate = RestTemplate(listOf(
			FormHttpMessageConverter(), tokenResponseHttpMessageConverter
		))
		restTemplate.errorHandler = OAuth2ErrorResponseErrorHandler()

		val tokenResponseClient = DefaultAuthorizationCodeTokenResponseClient()
		tokenResponseClient.setRestOperations(restTemplate)

		return tokenResponseClient
	}
}
