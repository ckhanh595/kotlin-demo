package com.sts.demo.configuration

import com.sts.demo.entity.UserEntity
import com.sts.demo.model.enums.UserRole
import com.sts.demo.model.enums.UserType
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
        val adminUsername = System.getenv("ADMIN_USERNAME")
        val adminPassword = System.getenv("ADMIN_PASSWORD")

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
