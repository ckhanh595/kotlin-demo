package com.sts.demo.model.dto.user

import com.sts.demo.model.enums.UserRole
import com.sts.demo.model.enums.UserType

data class CreateUserRequest(
	val username: String,
	val email: String,
	val fullName: String? = null,
	val password: String,
	val userRole: UserRole
) {
	val userType = UserType.LOCAL
}
