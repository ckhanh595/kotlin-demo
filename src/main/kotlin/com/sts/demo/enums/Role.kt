package com.sts.demo.enums

enum class Role {
	ADMIN, SUPPORTER, CUSTOMER;

	fun canManageRole(targetRole: Role): Boolean = when (this) {
		ADMIN -> true  // Admin can manage all roles
		SUPPORTER -> targetRole in listOf(SUPPORTER, CUSTOMER)  // Supporter can manage supporter and customer
		CUSTOMER -> false  // Customer can't manage any roles
	}
}