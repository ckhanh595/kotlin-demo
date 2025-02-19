package com.sts.demo.model.enums

enum class UserRole {
	ADMIN, SUPPORTER, CUSTOMER;

	fun canManageRole(targetUserRole: UserRole): Boolean = when (this) {
		UserRole.ADMIN -> true  // Admin can manage all roles
		UserRole.SUPPORTER -> targetUserRole in listOf(
			UserRole.SUPPORTER,
			UserRole.CUSTOMER
		)  // Supporter can manage supporter and customer
		UserRole.CUSTOMER -> false  // Customer can't manage any roles
	}
}