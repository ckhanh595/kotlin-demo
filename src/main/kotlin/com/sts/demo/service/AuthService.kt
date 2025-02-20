package com.sts.demo.service

import com.sts.demo.model.enums.UserRole
import org.springframework.security.core.Authentication

interface AuthService {
	fun getCurrentUserRole(authentication: Authentication): UserRole
}