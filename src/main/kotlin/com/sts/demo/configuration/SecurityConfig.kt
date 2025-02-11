package com.sts.demo.configuration

import com.sts.demo.service.CustomOAuth2UserService
import com.sts.demo.service.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig(
	private val customUserDetailsService: CustomUserDetailsService,
	private val customOAuth2UserService: CustomOAuth2UserService
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
			.oauth2Login { oauth2 ->
				oauth2
					.loginPage("/login")
					.userInfoEndpoint { it.userService(customOAuth2UserService) }
					.defaultSuccessUrl("/dashboard", true)
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
}
