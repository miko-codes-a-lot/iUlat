package dev.cloudants.iulat.lib.services


interface EmailService {
    suspend fun requestPasswordResetToken(email: String): Boolean
    suspend fun verifyResetToken(email: String, token: String): Boolean
    suspend fun resetPassword(email: String, token: String, newPassword: String): Boolean
}