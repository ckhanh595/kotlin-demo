package com.sts.demo.configuration

import com.sts.demo.entity.UserEntity
import com.sts.demo.model.enums.UserRole
import com.sts.demo.model.enums.UserType
import com.sts.demo.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.env.Environment
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val env: Environment
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val adminUsername = System.getenv("ADMIN_USERNAME") ?: env.getProperty("app.admin.username")
        val adminPassword = System.getenv("ADMIN_PASSWORD") ?: env.getProperty("app.admin.password")

        if (adminUsername == null || adminPassword == null) {
            println("Admin credentials not configured, skipping admin user creation")
            return
        }

        if (userRepository.findByUsername(adminUsername) == null) {
            val adminUserEntity = UserEntity(
                username = adminUsername,
                password = passwordEncoder.encode(adminPassword),
                email = "admin@sts.co",
                fullName = "Administrator",
                userType = UserType.LOCAL,
                userRole = UserRole.ADMIN
            )
            userRepository.save(adminUserEntity)
            println("Default admin created: $adminUsername (Use the password from environment variables)")
        } else {
            println("Admin user already exists, skipping creation.")
        }
    }
}
