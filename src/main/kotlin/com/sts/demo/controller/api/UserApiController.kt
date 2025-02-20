package com.sts.demo.controller.api

import com.sts.demo.model.dto.CreateUserRequest
import com.sts.demo.model.dto.CreateUserResponse
import com.sts.demo.service.AuthService
import com.sts.demo.service.UserManagementService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserApiController(
	private val userManagementService: UserManagementService,
	private val authService: AuthService
) {

	@PostMapping("/create")
	@PreAuthorize("hasAnyRole('ADMIN', 'SUPPORTER')")
	fun createUser(
		authentication: Authentication,
		@RequestBody createUserRequest: CreateUserRequest
	): ResponseEntity<CreateUserResponse> {
		val creatorRole = authService.getCurrentUserRole(authentication)
		val response = userManagementService.createUser(creatorRole, createUserRequest)

		return ResponseEntity.ok(response)
	}
}