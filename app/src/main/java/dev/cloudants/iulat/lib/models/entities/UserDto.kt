package dev.cloudants.iulat.lib.models.entities

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String?,
    val username: String,
    val password: String?,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val email: String,
    val mobileNumber: String,
    val gender: String,
    val address: AddressDto,
    val type: String,
    val role: String,
)
