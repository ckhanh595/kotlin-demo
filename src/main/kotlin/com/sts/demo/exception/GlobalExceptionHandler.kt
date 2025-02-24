package com.sts.demo.exception

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse>{
		val errorResponse = ErrorResponse(
			error = "Validation Error",
			message = "Invalid input data",
		)

		return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
	}

	@ExceptionHandler(ConstraintViolationException::class)
	fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<Map<String, Any>> {
		val errors = ex.constraintViolations.map { it.propertyPath.toString() to it.message }.toMap()
		return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
	}

	@ExceptionHandler(HttpMessageNotReadableException::class)
	fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
		val errorResponse = ErrorResponse(
			error = "Invalid Request",
			message = "Invalid request body format or missing required fields",
		)

		return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
	}

	@ExceptionHandler(BadCredentialsException::class)
	fun handleHttpMessageNotReadable(ex: BadCredentialsException): ResponseEntity<ErrorResponse> {
		val errorResponse = ErrorResponse(
			error = "Unauthorized",
			message = ex.message
		)

		return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
	}

	@ExceptionHandler(Exception::class)
	fun handleGeneralException(ex: Exception): ResponseEntity<String> {
		return ResponseEntity(ex.message, HttpStatus.INTERNAL_SERVER_ERROR)
	}

}

data class ErrorResponse(
	val error: String,
	val message: String?,
	val timestamp: LocalDateTime = LocalDateTime.now(),
)