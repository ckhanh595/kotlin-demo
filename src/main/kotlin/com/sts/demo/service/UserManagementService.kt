package com.sts.demo.service

import com.sts.demo.entity.User
import com.sts.demo.enums.Role
import com.sts.demo.repository.UserRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserManagementService(
	private val userRepository: UserRepository,
	private val passwordEncoder: PasswordEncoder
) {

	fun createUser(creatorRole: Role, username: String, email: String, password: String, role: Role) : User {
		// Check if creator has permission to create this role
		if (!creatorRole.canManageRole(role)) {
			throw AccessDeniedException("You don't have permission to create users with role $role")
		}

		// Check if username already exists
		if (userRepository.existsByUsername(username)) {
			throw IllegalArgumentException("Username already exists")
		}

		// Check if email already exists
		if (userRepository.existsByEmail(email)) {
			throw IllegalArgumentException("Email already exists")
		}
		val user = User(
			username = username,
			email = email,
			password = passwordEncoder.encode(password),
			role = role
		)

		return userRepository.save(user)
	}

	fun getUsers(viewerRole: Role): List<User> = when (viewerRole) {
		Role.ADMIN, Role.SUPPORTER -> userRepository.findAll()
		Role.CUSTOMER -> throw AccessDeniedException("Customers cannot view user list")
	}

	fun getUserById(viewerRole: Role, userId: Long): User {
		val user = userRepository.findById(userId)
			.orElseThrow { NoSuchElementException("User not found") }

		when (viewerRole) {
			Role.ADMIN, Role.SUPPORTER -> return user
			Role.CUSTOMER -> {
				// Customers can only view their own profile
				if (user.id != userId) {
					throw AccessDeniedException("You don't have permission to view this user")
				}
				return user
			}
		}
	}

}