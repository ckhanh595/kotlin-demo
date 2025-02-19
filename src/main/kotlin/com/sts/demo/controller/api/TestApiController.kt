package com.sts.demo.controller.api

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
class TestApiController {

    @GetMapping("/public")
    fun publicEndpoint(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("message" to "This is a public API endpoint"))
    }

    @GetMapping("/user")
    fun userEndpoint(authentication: Authentication): ResponseEntity<Map<String, Any>> {
        val username = when (val principal = authentication.principal) {
            is UserDetails -> principal.username
            is String -> principal
            else -> principal.toString()
        }

        val response = mapOf(
            "message" to "This is a protected API endpoint",
            "username" to username,
            "authorities" to authentication.authorities.map { it.authority }
        )
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    fun adminEndpoint(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("message" to "This is an admin-only API endpoint"))
    }
}