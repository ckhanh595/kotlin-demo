package com.sts.demo.service

import com.sts.demo.entity.UserEntity
import com.sts.demo.model.dto.CreateUserRequest
import com.sts.demo.model.dto.CreateUserResponse
import com.sts.demo.model.enums.UserRole

interface UserManagementService {

	fun createUser(creatorUserRole: UserRole, request: CreateUserRequest): CreateUserResponse

	fun getUsers(viewerUserRole: UserRole): List<UserEntity>

	fun getUserById(viewerUserRole: UserRole, userId: Long): UserEntity

}
