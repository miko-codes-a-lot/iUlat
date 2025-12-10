package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.UserDto

interface AuthService {
    suspend fun login(email: String, password: String): UserDto?
    suspend fun requestOTP(email: String): Boolean
    suspend fun verifyResetToken(email: String, token: String): Boolean
}