package com.sts.demo.model.enums

enum class UserRole {
	ADMIN, SUPPORTER, CUSTOMER;

	fun canManageRole(targetUserRole: UserRole): Boolean = when (this) {
		ADMIN -> true  // Admin can manage all roles
		SUPPORTER -> targetUserRole in listOf(
			SUPPORTER,
			CUSTOMER
		)  // Supporter can manage supporter and customer
		CUSTOMER -> false  // Customer can't manage any roles
	}
}