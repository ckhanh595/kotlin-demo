package com.sts.demo.mapper

import com.sts.demo.entity.UserEntity
import com.sts.demo.model.dto.user.CreateUserResponse

fun UserEntity.toDto() = CreateUserResponse(
	id = id,
	username = username,
	email = email,
	userRole = userRole,
	userType = userType,
	fullName = fullName,
	oauth2Provider = oauth2Provider
)

