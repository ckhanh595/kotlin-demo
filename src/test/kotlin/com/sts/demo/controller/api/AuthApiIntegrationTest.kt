package com.sts.demo.controller.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.sts.demo.entity.UserEntity
import com.sts.demo.model.dto.auth.AuthRequest
import com.sts.demo.model.enums.UserRole
import com.sts.demo.model.enums.UserType
import com.sts.demo.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthApiIntegrationTest {

	private val loginPath = "/api/auth/login"

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var objectMapper: ObjectMapper

	@Autowired
	private lateinit var userRepository: UserRepository

	@Autowired
	private lateinit var passwordEncoder: PasswordEncoder

	private val testUsername = "testuser"

	private val testPassword = "testpassword"

	@BeforeEach
	fun setUp() {
		userRepository.deleteAll()
	}

	@Test
	fun `login with valid credentials should return JWT token and user details`() {
		// Arrange
		createUser(testUsername, testPassword)
		val authRequest = AuthRequest(testUsername, testPassword)

		// Act & Assert
		mockMvc.perform(
			post(loginPath)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authRequest))
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.token").isString)
			.andExpect(jsonPath("$.username").value(testUsername))
			.andExpect(jsonPath("$.roles").isArray)
			.andExpect(jsonPath("$.roles[0]").value(UserRole.CUSTOMER.name))
	}

	@Test
	fun `login with invalid password should return 401`() {
		// Arrange
		createUser(testUsername, testPassword)
		val loginRequest = AuthRequest(testUsername, "wrongpassword")

		// Act & Assert
		mockMvc.perform(
			post(loginPath)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest))
		)
			.andExpect(status().isUnauthorized)
			.andExpect(jsonPath("$.error").value("Unauthorized"))
			.andExpect(jsonPath("$.message").value("Incorrect username or password"))
			.andExpect(jsonPath("$.timestamp").isNotEmpty)
	}

	@Test
	fun `login with non-existent user should return 401`() {
		// Arrange
		val loginRequest = AuthRequest("nonexistentuser", "wrongpassword")

		// Act & Assert
		mockMvc.perform(
			post(loginPath)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest))
		)
			.andExpect(status().isUnauthorized)
			.andExpect(jsonPath("$.error").value("Unauthorized"))
			.andExpect(jsonPath("$.message").value("Incorrect username or password"))
			.andExpect(jsonPath("$.timestamp").isNotEmpty)
	}

	fun createUser(username: String, password: String): UserEntity {
		val user = UserEntity(
			username = username,
			password = passwordEncoder.encode(password),
			email = "test@example.com",
			fullName = "John Doe",
			userRole = UserRole.CUSTOMER,
			userType = UserType.LOCAL
		)

		return userRepository.save(user)
	}
}
