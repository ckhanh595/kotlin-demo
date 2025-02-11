package com.sts.demo.configuration

import com.sts.demo.entity.User
import com.sts.demo.enums.Role
import com.sts.demo.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val adminUsername = System.getenv("ADMIN_USERNAME") ?: "ad"
        val adminPassword = System.getenv("ADMIN_PASSWORD") ?: "ad123"

        if (userRepository.findByUsername(adminUsername) == null) {
            val adminUser = User(
                username = adminUsername,
                password = passwordEncoder.encode(adminPassword),
                email = "admin@sts.co",
                role = Role.ADMIN
            )
            userRepository.save(adminUser)
            println("Default admin created: $adminUsername (Use the password from environment variables)")
        } else {
            println("Admin user already exists, skipping creation.")
        }
    }
}
