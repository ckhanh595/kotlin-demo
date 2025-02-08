package com.sts.demo.model

import java.time.LocalDate

data class CreateCustomerRequest(
	val firstName: String,
	val lastName: String,
	val email: String,
	val phoneNumber: String?,
	val address: String?,
	val city: String?,
	val country: String?,
	val birthDate: LocalDate?
)
