package com.sts.demo.service

import com.sts.demo.entity.Customer
import com.sts.demo.model.CreateCustomerRequest
import com.sts.demo.model.CustomerResponse
import com.sts.demo.repository.CustomerRepository
import org.springframework.stereotype.Service

@Service
class CustomerService(private val customerRepository: CustomerRepository) {

	fun getSampleText(): String {
		return "Hello, World!"
	}

	fun createCustomer(request: CreateCustomerRequest): CustomerResponse {
		val customer = Customer(
			firstName = request.firstName,
			lastName = request.lastName,
			email = request.email,
			phoneNumber = request.phoneNumber,
			address = request.address,
			city = request.city,
			country = request.country,
			birthDate = request.birthDate
		)
		val savedCustomer = customerRepository.save(customer)

		return toResponse(savedCustomer)
	}

}

fun toResponse(customer: Customer): CustomerResponse {
	return CustomerResponse(
		id = customer.id!!,
		firstName = customer.firstName!!,
		lastName = customer.lastName!!,
		email = customer.email!!,
		phoneNumber = customer.phoneNumber,
		address = customer.address,
		city = customer.city,
		country = customer.country,
		birthDate = customer.birthDate?.toString()
	)
}
