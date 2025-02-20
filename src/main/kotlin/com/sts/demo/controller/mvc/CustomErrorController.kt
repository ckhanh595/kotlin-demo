package com.sts.demo.controller.mvc

import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class CustomErrorController : ErrorController {

    @GetMapping("/error")
    fun handleError(request: HttpServletRequest, model: Model): String {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) as? Int ?: 500

        return when (status) {
            HttpStatus.FORBIDDEN.value() -> {
                model.addAttribute("errorTitle", "Access Denied")
                model.addAttribute("errorMessage", "You do not have permission to access this page.")
                "error/403"
            }
            else -> {
                model.addAttribute("errorTitle", "Unexpected Error")
                model.addAttribute("errorMessage", "Something went wrong. Please try again later.")
                "error/default"
            }
        }
    }
}