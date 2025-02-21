package com.sts.demo.service

import com.sts.demo.model.dto.auth.AuthRequest
import com.sts.demo.model.dto.auth.AuthResponse
import com.sts.demo.model.enums.UserRole
import org.springframework.security.core.Authentication

interface AuthService {

	fun validateCredentials(authRequest: AuthRequest): AuthResponse

	fun getCurrentUserRole(authentication: Authentication): UserRole

}
