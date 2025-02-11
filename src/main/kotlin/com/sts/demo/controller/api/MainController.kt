package com.sts.demo.controller.api

import com.sts.demo.service.CustomerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MainController {

	@Autowired
	lateinit var customerService: CustomerService

	@RequestMapping("/hello")
	fun hello(): String {
		return customerService.getSampleText()
	}

}