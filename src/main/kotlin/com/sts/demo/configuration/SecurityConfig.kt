package com.sts.demo.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

	@Bean
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
		http
			.authorizeHttpRequests {
				it.requestMatchers("/", "/login", "/oauth2/**").permitAll()
				it.anyRequest().authenticated()
			}
			.oauth2Login(Customizer.withDefaults()) // Default OAuth2 login handling
			.logout { logout -> logout.logoutSuccessUrl("/").permitAll() }

		return http.build()
	}

}
