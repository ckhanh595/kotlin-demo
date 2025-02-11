package com.sts.demo.controller

import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class DashboardController {

	@GetMapping("/dashboard")
	fun dashboard(model: Model, authentication: Authentication?, csrfToken: CsrfToken): String {

		if (authentication != null && authentication.isAuthenticated) {
			val principal: Any = authentication.principal
			val isAdminOrSupporter: Boolean = authentication.authorities.any {
				it.authority in listOf("ROLE_ADMIN", "ROLE_SUPPORTER")
			}
			model.addAttribute("canManageUsers", isAdminOrSupporter)

			when (principal) {
				is UserDetails -> {
					model.addAttribute("username", principal.username)
					model.addAttribute("email", "123@gmail.com")
					model.addAttribute("authorities", principal.authorities)
				}

				is OAuth2User -> {
					model.addAttribute("username", getPrincipalUsername(principal))
					model.addAttribute("email", principal.attributes["email"])
					model.addAttribute("authorities", principal.authorities)
					val provider = (authentication as OAuth2AuthenticationToken).authorizedClientRegistrationId
					model.addAttribute("oauth2Provider", provider)
				}
			}

			model.addAttribute("csrf", csrfToken)
		}

		return "dashboard"
	}

	fun getPrincipalUsername(principal: OAuth2User): String? {
		return principal.attributes["login"] as? String ?: principal.attributes["name"] as? String
	}
}