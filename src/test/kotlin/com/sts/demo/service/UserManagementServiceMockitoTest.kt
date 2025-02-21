package com.sts.demo.service

import com.sts.demo.entity.UserEntity
import com.sts.demo.model.dto.user.CreateUserRequest
import com.sts.demo.model.enums.UserRole
import com.sts.demo.model.enums.UserType
import com.sts.demo.repository.UserRepository
import com.sts.demo.service.impl.UserManagementServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class UserManagementServiceMockitoTest {

	@Mock
	private lateinit var userRepository: UserRepository

	@Mock
	private lateinit var passwordEncoder: PasswordEncoder

	@InjectMocks
	private lateinit var userManagementService: UserManagementServiceImpl

	@Test
	fun `createUser should create a new user successfully when all validations pass`() {
		// Arrange
		val creatorRole = UserRole.ADMIN
		val request = createValidRequest()

		`when` (userRepository.existsByUsernameAndUserType(request.username, UserType.LOCAL)).thenReturn(false)
		`when` (userRepository.existsByEmailAndUserType(request.email, UserType.LOCAL)).thenReturn( false)
		`when`(passwordEncoder.encode(request.password)).thenReturn("encoded_password")
		val savedUser = mockSavedUser()
		`when`(userRepository.save(any())).thenReturn(savedUser)

		// Act
		val result = userManagementService.createUser(creatorRole, request)

		// Assert
		assertNotNull(result)
		assertEquals(savedUser.id, result.id)
		assertEquals(savedUser.username, result.username)
		assertEquals(savedUser.email, result.email)

		verify(userRepository).existsByUsernameAndUserType(request.username, request.userType)
		verify(userRepository).existsByEmailAndUserType(request.email, request.userType)
		verify(passwordEncoder).encode(request.password)
		verify(userRepository).save(any())
	}

	@Test
	fun `createUser should throw IllegalArgumentException when username already exists`() {
		// Arrange
		val request = createValidRequest()
		val creatorRole = UserRole.ADMIN

		// Mock repository to return true for username check
		`when`(userRepository.existsByUsernameAndUserType(request.username, request.userType)).thenReturn(true)

		// Act & Assert
		val exception = assertThrows(IllegalArgumentException::class.java) {
			userManagementService.createUser(creatorRole, request)
		}

		assertEquals("Username already exists", exception.message)
	}

	private fun createValidRequest() = CreateUserRequest(
		username = "testuser",
		email = "test@example.com",
		password = "password",
		fullName = "Test User",
		userRole = UserRole.ADMIN
	)

	private fun mockSavedUser() = UserEntity(
		id = 1L,
		username = "testuser",
		email = "test@example.com",
		password = "encoded_password",
		fullName = "Test User",
		userType = UserType.LOCAL,
		userRole = UserRole.CUSTOMER
	)
}
