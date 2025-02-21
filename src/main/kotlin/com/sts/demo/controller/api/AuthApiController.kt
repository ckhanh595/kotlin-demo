package com.sts.demo.controller.api

import com.sts.demo.model.dto.auth.AuthRequest
import com.sts.demo.model.dto.auth.AuthResponse
import com.sts.demo.security.JwtTokenUtil
import com.sts.demo.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthApiController(
	private val authService: AuthService,
	private val jwtTokenUtil: JwtTokenUtil
) {

	@PostMapping("/login")
	fun processLogin(@RequestBody authRequest: AuthRequest): ResponseEntity<AuthResponse> {
		val authResponse = authService.validateCredentials(authRequest)

		return ResponseEntity.ok(authResponse)
	}

	/**
	 * Get a JWT token for an already authenticated user (via session)
	 * This is useful for users who have logged in via form or OAuth2 and now want to use APIs
	 */
	@GetMapping("/token")
	fun getToken(): ResponseEntity<AuthResponse> {
		val authentication = SecurityContextHolder.getContext().authentication

		if (authentication.isAuthenticated) {
			val principal = authentication.principal

			when (principal) {
				is UserDetails -> {
					val token = jwtTokenUtil.generateToken(principal)
					val roles = principal.authorities.map { it.authority.replace("ROLE_", "") }
					return ResponseEntity.ok(AuthResponse(token, principal.username, roles))
				}

				is OAuth2User -> {
					if (authentication is OAuth2AuthenticationToken) {
						val username = when {
							principal.attributes.containsKey("email") -> principal.attributes["email"] as String
							principal.attributes.containsKey("login") -> principal.attributes["login"] as String
							principal.attributes.containsKey("name") -> principal.attributes["name"] as String
							else -> principal.name
						}

						val token = jwtTokenUtil.generateOAuth2Token(username, authentication.authorities)
						val roles = authentication.authorities.map { it.authority.replace("ROLE_", "") }

						return ResponseEntity.ok(AuthResponse(token, username, roles))
					}
				}
			}
		}

		return ResponseEntity.status(401).build()
	}

	/**
	 * Verify that the current JWT token is valid
	 */
	@GetMapping("/verify")
	fun verifyToken(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<Map<String, Any>> {
		val roles = userDetails.authorities.map { it.authority.replace("ROLE_", "") }

		val response = mapOf(
			"username" to userDetails.username,
			"authenticated" to true,
			"roles" to roles
		)

		return ResponseEntity.ok(response)
	}
}
