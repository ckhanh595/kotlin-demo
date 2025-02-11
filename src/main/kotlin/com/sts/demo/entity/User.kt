package com.sts.demo.entity

import com.sts.demo.enums.Role
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class User(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long? = null,

	@Column(unique = true, nullable = false)
	val username: String,

	@Column(nullable = false)
	var password: String?,

	@Column(unique = true)
	val email: String? = null,

	@Enumerated(EnumType.STRING)
	var role: Role? = null,

	@Column
	val oauth2Provider: String? = null
)
