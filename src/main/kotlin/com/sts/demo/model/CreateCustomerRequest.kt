package com.sts.demo.model

import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class CreateCustomerRequest(
	// TODO: doesn't work as Java
	@field:NotBlank(message = "First name is required")
	val firstName: String="",

//	@field:NotBlank(message = "Last name is required")
	val lastName: String,

//	@field:NotBlank(message = "Email is required")
//	@field:Email(message = "Invalid email format")
	val email: String,

//	@field:Size(min = 10, max = 13, message = "Phone number must be between 10 and 13 characters")
	val phoneNumber: String?,

//	@field:Size(max = 255, message = "Address must not exceed 255 characters")
	val address: String?,

//	@field:Size(max = 100, message = "City must not exceed 100 characters")
	val city: String?,

	val country: String?,
	val birthDate: LocalDate?
)
