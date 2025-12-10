package dev.cloudants.iulat.lib.services_impl

import android.util.Log
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.services.EmailService
import dev.cloudants.iulat.shared.api.RetrofitClient
import javax.inject.Inject

class EmailServiceImpl  @Inject constructor() : EmailService {
    override suspend fun requestPasswordResetToken(email: String): Boolean {
        return try {
            val userDto = UserDto(email = email.trim())
            val response = RetrofitClient.apiService.requestToken(userDto)
            Log.e("EmailServiceImpl", "Email API response: ${response.code()} ${response.message()}")
            response.isSuccessful
        } catch (exception: Exception) {
            exception.printStackTrace()
            Log.e("EmailServiceImpl", "Exception sending email", exception)
            false
        }
    }

    override suspend fun verifyResetToken(email: String, token: String): Boolean {
        return try {
            val requestBody = mapOf(
                "email" to email,
                "token" to token
            )
            val response = RetrofitClient.apiService.verifyToken(requestBody)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun resetPassword(email: String, token: String, newPassword: String): Boolean {
        return false
    }
}