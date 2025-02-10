package com.sts.demo.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

	@GetMapping("/")
	fun home(): String {
		return "Welcome to Spring Boot Kotlin Demo"
	}

	@GetMapping("/user")
	fun getUser(@AuthenticationPrincipal principal: OAuth2User?): Map<String, Any> {
		return principal?.attributes ?: mapOf("message" to "User not logged in")
	}
}
