package com.sts.demo.model.dto.user

import com.sts.demo.model.enums.SupportedOAuth2Provider
import com.sts.demo.model.enums.UserRole
import com.sts.demo.model.enums.UserType

data class CreateUserResponse (
	val id: Long?,
	val username: String?,
	val email: String?,
	val fullName: String?,
	val userRole: UserRole,
	val userType: UserType,
	val oauth2Provider: SupportedOAuth2Provider?
)
