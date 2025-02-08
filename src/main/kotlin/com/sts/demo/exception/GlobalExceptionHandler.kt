package com.sts.demo.exception

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse>{
		val errors = ex.bindingResult.fieldErrors.associate { error ->
			error.field to (error.defaultMessage ?: "Invalid value")
		}

		val errorResponse = ErrorResponse(
			status = HttpStatus.BAD_REQUEST.value(),
			error = "Validation Error",
			message = "Invalid input data",
			fields = errors
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
			status = HttpStatus.BAD_REQUEST.value(),
			error = "Invalid Request",
			message = "Invalid request body format or missing required fields",
			fields = mapOf("error" to (ex.message ?: "Invalid request body"))
		)

		return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
	}

	@ExceptionHandler(Exception::class)
	fun handleGeneralException(ex: Exception): ResponseEntity<String> {
		return ResponseEntity(ex.message, HttpStatus.INTERNAL_SERVER_ERROR)
	}

}

data class ErrorResponse(
	val timestamp: LocalDateTime = LocalDateTime.now(),
	val status: Int,
	val error: String,
	val message: String,
	val fields: Map<String, String>? = null
)