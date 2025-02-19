package com.sts.demo.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity {
	@Column(name = "created_at", updatable = false)
	var createdAt: LocalDateTime? = null

	@Column(name = "updated_at")
	var updatedAt: LocalDateTime? = null

	@PrePersist
	fun onPrePersist() {
		createdAt = LocalDateTime.now()
		updatedAt = LocalDateTime.now()
	}

	@PreUpdate
	fun onPreUpdate() {
		updatedAt = LocalDateTime.now()
	}
}
