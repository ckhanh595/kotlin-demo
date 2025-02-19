package com.sts.demo.controller

import com.sts.demo.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AuthController(private val userRepository: UserRepository) {

	@GetMapping("/")
	fun home() = "redirect:/login"

	@GetMapping("/login")
	fun login(request: HttpServletRequest, error: String?, model: Model, logout: String?): String {
		if (error != null) {
			model.addAttribute("error", true)
			model.addAttribute("errorMessage", "Your username or password is invalid.")
		}

		if (logout != null) {
			model.addAttribute("logout", true)
			model.addAttribute("logoutMessage", "You have been logged out.")
		}

		val csrf = request.getAttribute(CsrfToken::class.java.name) as? CsrfToken
		csrf?.let {
			model.addAttribute("csrf", it)
		}

		return "login"
	}

}