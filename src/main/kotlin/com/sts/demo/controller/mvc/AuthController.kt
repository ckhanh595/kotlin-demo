package com.sts.demo.controller.mvc

import com.sts.demo.model.constants.Message
import com.sts.demo.model.constants.ModelAttribute
import com.sts.demo.model.constants.ModelAttribute.CSRF
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller
class AuthController {

	@GetMapping("/")
	fun home() = "redirect:/login"

	@GetMapping("/login")
	fun login(@RequestParam error: String?, model: Model, csrfToken: CsrfToken): String {
		error?.let {
			model.addAttribute(ModelAttribute.ERROR, true)
			model.addAttribute(ModelAttribute.ERROR_MESSAGE, Message.LOGIN_ERROR)
		}
		model.addAttribute(CSRF, csrfToken)

		return "login"
	}
}
