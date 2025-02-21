package com.sts.demo.service

import com.sts.demo.entity.UserEntity
import com.sts.demo.model.dto.user.CreateUserRequest
import com.sts.demo.model.enums.UserRole
import com.sts.demo.model.enums.UserType
import com.sts.demo.repository.UserRepository
import com.sts.demo.service.impl.UserManagementServiceImpl
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserManagementServiceTest {

	private lateinit var userRepository: UserRepository
	private lateinit var passwordEncoder: PasswordEncoder
	private lateinit var userManagementService: UserManagementService

	@BeforeEach
	fun setUp() {
		userRepository = mockk()
		passwordEncoder = mockk()
		userManagementService = UserManagementServiceImpl(userRepository, passwordEncoder)
	}

	@Test
	fun `createUser should create a new user successfully when all validations pass`() {
		// Arrange
		val creatorRole = UserRole.ADMIN
		val request = createValidRequest(UserRole.SUPPORTER)
		every { userRepository.existsByUsernameAndUserType(request.username, UserType.LOCAL) } returns false
		every { userRepository.existsByEmailAndUserType(request.email, UserType.LOCAL) } returns false
		every { passwordEncoder.encode(request.password) } returns "encoded_password"
		val savedUser = createUserEntity()
		every { userRepository.save(any()) } returns savedUser

		// Act
		val result = userManagementService.createUser(creatorRole, request)

		// Assert
		assertNotNull(result)
		assertEquals(savedUser.id, result.id)
		assertEquals(savedUser.username, result.username)
		assertEquals(savedUser.email, result.email)
		verify { userRepository.existsByUsernameAndUserType(request.username, UserType.LOCAL) }
		verify { userRepository.existsByEmailAndUserType(request.email, UserType.LOCAL) }
		verify { passwordEncoder.encode(request.password) }
		verify { userRepository.save(any()) }
	}

	@Test
	fun `createUser should throw AccessDeniedException when creator role doesn't have permission to create user with requested role`() {
		// Arrange
		val creatorRole = UserRole.CUSTOMER
		val request = createValidRequest(UserRole.SUPPORTER)

		// Act and Assert
		val exception = assertThrows(AccessDeniedException::class.java) {
			userManagementService.createUser(creatorRole, request)
		}
		assertEquals("You don't have permission to create users with role ${request.userRole}", exception.message)
		verify { userRepository wasNot Called }
		verify { passwordEncoder wasNot Called }
	}

	@Test
	fun `createUser should throw IllegalArgumentException when username already exists`() {
		// Arrange
		val creatorRole = UserRole.ADMIN
		val request = createValidRequest(UserRole.SUPPORTER)

		every { userRepository.existsByUsernameAndUserType(request.username, UserType.LOCAL) } returns true

		// Act & Assert
		val exception = assertThrows(IllegalArgumentException::class.java) {
			userManagementService.createUser(creatorRole, request)
		}

		assertEquals("Username already exists", exception.message)
		verify(exactly = 1) { userRepository.existsByUsernameAndUserType(request.username, UserType.LOCAL) }
		verify(exactly = 0) { userRepository.existsByEmailAndUserType(any(), any()) }
		verify(exactly = 0) { userRepository.save(any()) }
	}

	@Test
	fun `createUser should throw IllegalArgumentException when email already exists`() {
		// Arrange
		val creatorRole = UserRole.ADMIN
		val request = createValidRequest(UserRole.SUPPORTER)

		every { userRepository.existsByUsernameAndUserType(request.username, UserType.LOCAL) } returns false
		every { userRepository.existsByEmailAndUserType(request.email, UserType.LOCAL) } returns true

		// Act & Assert
		val exception = assertThrows(IllegalArgumentException::class.java) {
			userManagementService.createUser(creatorRole, request)
		}

		assertEquals("Email already exists", exception.message)
		verify(exactly = 1) { userRepository.existsByUsernameAndUserType(request.username, UserType.LOCAL) }
		verify(exactly = 1) { userRepository.existsByEmailAndUserType(request.email, UserType.LOCAL) }
		verify(exactly = 0) { userRepository.save(any()) }
	}

	private fun createValidRequest(userRole: UserRole) = CreateUserRequest(
		username = "testuser",
		email = "test@example.com",
		password = "password",
		fullName = "Test User",
		userRole = userRole
	)

	private fun createUserEntity() = UserEntity(
		id = 1L,
		username = "testuser",
		email = "test@example.com",
		password = "encoded_password",
		fullName = "Test User",
		userType = UserType.LOCAL,
		userRole = UserRole.CUSTOMER
	)
}
