package com.sts.demo.controller.api

import com.sts.demo.model.CreateCustomerRequest
import com.sts.demo.model.CustomerResponse
import com.sts.demo.service.CustomerService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/customers")
class CustomerResource(private val customerService: CustomerService) {

	@PostMapping
	fun createCustomer(@Valid @RequestBody  request: CreateCustomerRequest) : ResponseEntity<CustomerResponse> {
		return ResponseEntity.ok(customerService.createCustomer(request))
	}

}