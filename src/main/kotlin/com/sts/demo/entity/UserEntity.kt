package com.sts.demo.entity

import com.sts.demo.model.enums.SupportedOAuth2Provider
import com.sts.demo.model.enums.UserRole
import com.sts.demo.model.enums.UserType
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
data class UserEntity(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	val id: Long? = null,

	@Column(name = "username", nullable = true)
	val username: String? = null,

	@Column(name = "password", nullable = false)
	var password: String,

	@Column(name = "email", nullable = true)
	val email: String? = null,

	@Column(name = "full_name", nullable = true)
	val fullName: String? = null,

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	var userRole: UserRole,

	@Enumerated(EnumType.STRING)
	@Column(name = "user_type", nullable = false)
	val userType: UserType,

	@Enumerated(EnumType.STRING)
	@Column(name = "oauth2_provider", nullable = true)
	var oauth2Provider: SupportedOAuth2Provider? = null
) : BaseEntity()
