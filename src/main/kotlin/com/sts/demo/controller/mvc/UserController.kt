package com.sts.demo.controller.mvc

import com.sts.demo.model.dto.CreateUserRequest
import com.sts.demo.model.enums.UserRole
import com.sts.demo.service.AuthService
import com.sts.demo.service.UserManagementService
import org.springframework.security.core.Authentication
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/users")
class UserController(
	private val userManagementService: UserManagementService,
	private val authService: AuthService
) {

	@PostMapping("/create")
	fun createUser(
		@RequestParam username: String,
		@RequestParam email: String,
		@RequestParam password: String,
		@RequestParam fullName: String?,
		@RequestParam userRole: UserRole,
		authentication: Authentication,
		redirectAttributes: RedirectAttributes
	): String {
		try {
			val creatorRole = authService.getCurrentUserRole(authentication)
			val createUserRequest = CreateUserRequest(
				username = username,
				email = email,
				password = password,
				fullName = fullName,
				userRole = userRole
			)

			userManagementService.createUser(creatorRole, createUserRequest)
			redirectAttributes.addFlashAttribute("success", "User created successfully")
		} catch (e: Exception) {
			redirectAttributes.addFlashAttribute("error", e.message)
		}

		return "redirect:/users"
	}

	@GetMapping
	fun getUsers(authentication: Authentication, model: Model, csrfToken: CsrfToken): String {
		val role = authService.getCurrentUserRole(authentication)
		val users = userManagementService.getUsers(role)

		model.addAttribute("users", users)
		model.addAttribute("roles", UserRole.entries.toTypedArray())
		model.addAttribute("csrf", csrfToken)

		return "user-management"
	}

//	@GetMapping("/{id}")
//	fun getUser(@PathVariable id: Long, authentication: Authentication): UserDTO {
//		val role = getCurrentUserRole(authentication)
//
//		return userManagementService.getUserById(role, id).toDTO()
//	}
//
//	@PutMapping("/{id}")
//	fun updateUser(
//		@PathVariable id: Long,
//		@RequestBody request: UpdateUserRequest,
//		authentication: Authentication
//	): UserDTO {
//		val updaterRole = getCurrentUserRole(authentication)
//		return userManagementService.updateUser(
//			updaterRole = updaterRole,
//			userId = id,
//			updates = UserUpdateDTO(
//				email = request.email,
//				password = request.password,
//				role = request.role
//			)
//		).toDTO()
//	}
//
//	@DeleteMapping("/{id}")
//	fun deleteUser(@PathVariable id: Long, authentication: Authentication) {
//		val deleterRole = getCurrentUserRole(authentication)
//		userManagementService.deleteUser(deleterRole, id)
//	}


}
