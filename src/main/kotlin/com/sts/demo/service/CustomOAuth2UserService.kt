package com.sts.demo.service

import com.sts.demo.entity.User
import com.sts.demo.enums.Role
import com.sts.demo.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomOAuth2UserService(
	private val userRepository: UserRepository,
	private val passwordEncoder: PasswordEncoder
) : DefaultOAuth2UserService() {

	override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
		val oAuth2User = super.loadUser(userRequest)

		val provider = userRequest.clientRegistration.registrationId
		val email = when (provider) {
			"google" -> oAuth2User.attributes["email"] as String
			"github" -> oAuth2User.attributes["email"] as? String ?: ""
			"microsoft" -> oAuth2User.attributes["email"] as String
			"facebook" -> oAuth2User.attributes["email"] as String
			else -> throw IllegalArgumentException("Unsupported provider: $provider")
		}

		val username = when (provider) {
			"google" -> email.substringBefore("@")
			"github" -> oAuth2User.attributes["login"] as String
			"microsoft" -> email.substringBefore("@")
			"facebook" -> {
				oAuth2User.attributes["name"] as? String
					?: email.substringBefore("@")
			}
			else -> throw IllegalArgumentException("Unsupported provider: $provider")
		}

		var user = userRepository.findByEmail(email)

		if (user == null) {
			user = User(
				username = username,
				email = email,
				password = passwordEncoder.encode(UUID.randomUUID().toString()),
				role = Role.CUSTOMER,
				oauth2Provider = provider
			)

			userRepository.save(user)
			println("Created new OAuth2 user: $email with provider: $provider")

		} else {
			if (user.oauth2Provider == null) {
				user.oauth2Provider = provider
				userRepository.save(user)
				println("Updated existing user with OAuth2 provider: $provider")
			}
		}

		return oAuth2User
	}
}