package com.sts.demo.model.dto.auth

data class AuthResponse(
	val token: String,
	val username: String,
	val roles: List<String>
)
