package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.UserDto

interface UserService {
    fun findOne(id: String): UserDto
    fun findAll(): List<UserDto>
    fun create(user: UserDto): UserDto
    fun update(id: String, user: UserDto): UserDto
    fun delete(id: String): Boolean
}