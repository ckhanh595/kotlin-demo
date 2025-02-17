package com.sts.demo.configuration.oauth2

import com.sts.demo.entity.User
import com.sts.demo.enums.Role
import com.sts.demo.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.util.*

@Component
class OAuth2LoginSuccessHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        if (authentication is OAuth2AuthenticationToken) {
            val provider = authentication.authorizedClientRegistrationId
            val oauth2User = authentication.principal
//            val email = authentication.principal.attributes["email"] as String
            // Extract email based on provider
            val email = when (provider) {
                "google" -> oauth2User.attributes["email"] as String
                "github" -> oauth2User.attributes["email"] as? String ?: ""
                "microsoft" -> oauth2User.attributes["email"] as String
                "facebook" -> oauth2User.attributes["email"] as String
                else -> ""
            }

            // Extract username based on provider
            val username = when (provider) {
                "google" -> email.substringBefore("@")
                "github" -> oauth2User.attributes["login"] as String
                "microsoft" -> email.substringBefore("@")
                "facebook" -> {
                    oauth2User.attributes["name"] as? String
                        ?: email.substringBefore("@")
                }
                else -> email.substringBefore("@")
            }
            println("OAuth2 Login Success: $provider, $email, $username")


            // Find or create user
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
                logger.debug("Created new OAuth2 user: $email with provider: $provider")
            } else if (user.oauth2Provider == null) {
                user.oauth2Provider = provider
                userRepository.save(user)
                logger.debug("Updated existing user with OAuth2 provider: $provider")
            }

        }

        println("OAuth2 Login Success")
        println("Authentication: $authentication")
        // Set the default target URL and redirect
        defaultTargetUrl = "/dashboard"
        super.onAuthenticationSuccess(request, response, authentication)
    }
}