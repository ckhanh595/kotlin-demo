package com.sts.demo.repository

import com.sts.demo.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
	fun findByUsername(username: String): UserEntity?
	fun existsByUsername(username: String): Boolean
	fun existsByEmail(email: String): Boolean
	fun findByEmail(email: String): UserEntity?
}
