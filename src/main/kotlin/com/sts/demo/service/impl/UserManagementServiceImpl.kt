package com.sts.demo.service.impl

import com.sts.demo.entity.UserEntity
import com.sts.demo.mapper.toDto
import com.sts.demo.model.dto.user.CreateUserRequest
import com.sts.demo.model.dto.user.CreateUserResponse
import com.sts.demo.model.enums.UserRole
import com.sts.demo.repository.UserRepository
import com.sts.demo.service.UserManagementService
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserManagementServiceImpl(
	private val userRepository: UserRepository,
	private val passwordEncoder: PasswordEncoder
) : UserManagementService {

	override fun createUser(creatorUserRole: UserRole, request: CreateUserRequest): CreateUserResponse {
		validateCreatorRole(creatorUserRole, request)
		validateUsernameExisting(request)
		validateEmailExisting(request)

		val userEntity = toUserEntity(request)

		return userRepository.save(userEntity).toDto()
	}

	private fun validateCreatorRole(creatorUserRole: UserRole, request: CreateUserRequest) {
		if (!creatorUserRole.canManageRole(request.userRole)) {
			throw AccessDeniedException("You don't have permission to create users with role ${request.userRole}")
		}
	}

	private fun validateUsernameExisting(request: CreateUserRequest) {
		if (userRepository.existsByUsernameAndUserType(request.username, request.userType)) {
			throw IllegalArgumentException("Username already exists")
		}
	}

	private fun validateEmailExisting(request: CreateUserRequest) {
		if (userRepository.existsByEmailAndUserType(request.email, request.userType)) {
			throw IllegalArgumentException("Email already exists")
		}
	}

	private fun toUserEntity(request: CreateUserRequest): UserEntity {
		return UserEntity(
			username = request.username,
			email = request.email,
			password = passwordEncoder.encode(request.password),
			fullName = request.fullName,
			userType = request.userType,
			userRole = request.userRole
		)
	}

	override fun getUsers(viewerUserRole: UserRole): List<UserEntity> = when (viewerUserRole) {
		UserRole.ADMIN, UserRole.SUPPORTER -> userRepository.findAll()
		UserRole.CUSTOMER -> throw AccessDeniedException("Customers cannot view user list")
	}

	override fun getUserById(viewerUserRole: UserRole, userId: Long): UserEntity {
		val user = userRepository.findById(userId)
			.orElseThrow { NoSuchElementException("User not found") }

		when (viewerUserRole) {
			UserRole.ADMIN, UserRole.SUPPORTER -> return user
			UserRole.CUSTOMER -> {
				// Customers can only view their own profile
				if (user.id != userId) {
					throw AccessDeniedException("You don't have permission to view this user")
				}
				return user
			}
		}
	}
}
