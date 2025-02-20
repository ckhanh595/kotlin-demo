package com.sts.demo.service.impl

import com.sts.demo.model.enums.UserRole
import com.sts.demo.service.AuthService
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl : AuthService {

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