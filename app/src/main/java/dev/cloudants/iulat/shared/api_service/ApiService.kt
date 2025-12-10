package dev.cloudants.iulat.shared.api_service

import dev.cloudants.iulat.lib.models.entities.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
//    @POST("request-token")
    @POST("api/request-token")
    suspend fun requestToken(@Body userDto: UserDto): Response<Any>

//    @POST("verify-token")
    @POST("api/verify-token")
    suspend fun verifyToken(@Body requestBody: Map<String, String>): Response<Any>

    @POST("api/reset-password")
    suspend fun resetPassword(@Body requestBody: Map<String, String>): Response<Any>
}