package com.sts.demo.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
data class Customer (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val firstName: String?,

    @Column(nullable = false)
    val lastName: String?,

    @Column(nullable = false)
    val email: String?,

    val phoneNumber: String?,
    val address: String?,
    val city: String?,
    val country: String?,
    val birthDate: LocalDate?,
    val createdAt: LocalDate = LocalDate.now()
)
