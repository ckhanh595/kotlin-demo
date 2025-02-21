package com.sts.demo.service.impl

import com.sts.demo.model.dto.auth.AuthRequest
import com.sts.demo.model.dto.auth.AuthResponse
import com.sts.demo.model.enums.UserRole
import com.sts.demo.security.JwtTokenUtil
import com.sts.demo.service.AuthService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
	private val authenticationManager: AuthenticationManager,
	private val jwtTokenUtil: JwtTokenUtil
) : AuthService {

	override fun validateCredentials(authRequest: AuthRequest): AuthResponse {
		try {
			val authenticationToken = UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
			val authentication = authenticationManager.authenticate(authenticationToken)
			val principal: Any = authentication.principal

			if (principal !is UserDetails) {
				throw IllegalStateException("Unknown principal type")
			}

			val token = jwtTokenUtil.generateToken(principal)
			val roles = principal.authorities.map { it.authority.replace("ROLE_", "") }

			return AuthResponse(token, principal.username, roles)
		} catch (e: BadCredentialsException) {
			throw BadCredentialsException("Incorrect username or password")
		}
	}

	override fun getCurrentUserRole(authentication: Authentication): UserRole {
		return when (val principal = authentication.principal) {
			is UserDetails -> UserRole.valueOf(
				principal.authorities.first().authority.removePrefix("ROLE_")
			)

			is OAuth2User -> UserRole.CUSTOMER
			else -> throw IllegalStateException("Unknown principal type")
		}
	}

}