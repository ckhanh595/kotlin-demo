package com.sts.demo.controller

import com.sts.demo.entity.User
import com.sts.demo.enums.Role
import com.sts.demo.service.UserManagementService
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/users")
class UserController(private val userManagementService: UserManagementService) {

	@GetMapping
	fun getUsers(authentication: Authentication, model: Model, csrfToken: CsrfToken): String {
		val role = getCurrentUserRole(authentication)
		val users = userManagementService.getUsers(role)

		model.addAttribute("users", users)
		model.addAttribute("roles", Role.entries.toTypedArray())
		model.addAttribute("csrf", csrfToken)

		return "user-management"
	}

	@GetMapping("/{id}")
	fun getUser(@PathVariable id: Long, authentication: Authentication): UserDTO {
		val role = getCurrentUserRole(authentication)

		return userManagementService.getUserById(role, id).toDTO()
	}

	@PostMapping("/create")
	fun createUser(
		@RequestParam username: String,
		@RequestParam email: String,
		@RequestParam password: String,
		@RequestParam role: Role,
		authentication: Authentication,
		redirectAttributes: RedirectAttributes
	): String {
		try {
			val creatorRole = getCurrentUserRole(authentication)

			userManagementService.createUser(
				creatorRole = creatorRole,
				username = username,
				email = email,
				password = password,
				role = role
			)
			redirectAttributes.addFlashAttribute("success", "User created successfully")
		} catch (e: Exception) {
			redirectAttributes.addFlashAttribute("error", e.message)
		}

		return "redirect:/users"
	}
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

	private fun getCurrentUserRole(authentication: Authentication): Role {
		return when (val principal = authentication.principal) {
			is UserDetails -> Role.valueOf(
				principal.authorities.first().authority.removePrefix("ROLE_")
			)

			is OAuth2User -> Role.CUSTOMER  // OAuth2 users are always customers
			else -> throw IllegalStateException("Unknown principal type")
		}
	}
}

data class CreateUserRequest(
	val username: String,
	val email: String,
	val password: String,
	val role: Role
)

data class UserDTO(
	val id: Long?,
	val username: String,
	val email: String?,
	val role: Role?,
	val oauth2Provider: String?
)


fun User.toDTO() = UserDTO(
	id = id,
	username = username,
	email = email,
	role = role,
	oauth2Provider = oauth2Provider
)