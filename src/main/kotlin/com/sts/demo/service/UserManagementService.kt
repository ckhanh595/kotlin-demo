package com.sts.demo.service

import com.sts.demo.entity.UserEntity
import com.sts.demo.model.enums.UserRole
import com.sts.demo.model.enums.UserType
import com.sts.demo.repository.UserRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserManagementService(
	private val userRepository: UserRepository,
	private val passwordEncoder: PasswordEncoder
) {

	fun createUser(creatorUserRole: UserRole, username: String, email: String, password: String, userRole: UserRole) : UserEntity {
		// Check if creator has permission to create this role
		if (!creatorUserRole.canManageRole(userRole)) {
			throw AccessDeniedException("You don't have permission to create users with role $userRole")
		}

		// Check if username already exists
		if (userRepository.existsByUsername(username)) {
			throw IllegalArgumentException("Username already exists")
		}

		// Check if email already exists
		if (userRepository.existsByEmail(email)) {
			throw IllegalArgumentException("Email already exists")
		}
		val userEntity = UserEntity(
			username = username,
			email = email,
			password = passwordEncoder.encode(password),
			userType = UserType.LOCAL,
			userRole = userRole
		)

		return userRepository.save(userEntity)
	}

	fun getUsers(viewerUserRole: UserRole): List<UserEntity> = when (viewerUserRole) {
		UserRole.ADMIN, UserRole.SUPPORTER -> userRepository.findAll()
		UserRole.CUSTOMER -> throw AccessDeniedException("Customers cannot view user list")
	}

	fun getUserById(viewerUserRole: UserRole, userId: Long): UserEntity {
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