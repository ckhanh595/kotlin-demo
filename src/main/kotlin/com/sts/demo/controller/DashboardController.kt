package com.sts.demo.controller

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class DashboardController{

	@GetMapping("/dashboard")
	fun dashboard(model: Model, authentication: Authentication?): String {

		if (authentication != null && authentication.isAuthenticated) {
			val principal = authentication.principal

			if (principal is org.springframework.security.core.userdetails.UserDetails) {
				model.addAttribute("username", principal.username)
				model.addAttribute("email", "123@gmail.com")

				model.addAttribute("authorities", principal.authorities)
			} else if (principal is OAuth2User) {
				model.addAttribute("username", getPrincipalUsername(principal))
				model.addAttribute("email", principal.attributes["email"])
				model.addAttribute("authorities", principal.authorities)
				val provider = (authentication as OAuth2AuthenticationToken).authorizedClientRegistrationId
				model.addAttribute("oauth2Provider", provider)
			}
		}

		return "dashboard"
	}

	fun getPrincipalUsername(principal: OAuth2User): String? {
		return principal.attributes["login"] as? String ?: principal.attributes["name"] as? String
	}
}