package dev.cloudants.iulat.lib.models.entities

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String? = null,
    val username: String = "",
    val password: String = "",
    val firstName: String = "",
    val middleName: String? = null,
    val lastName: String = "",
    val email: String = "",
    val mobileNumber: String? = null,
    var dateOfBirth: String = "",
    var userProfile: String? = null,
    val gender: String,
    var validId: String? = null,
    val address: AddressDto,
    val type: String,
    var isAdmin: Boolean = false,
    var isResidence: Boolean = false,
)
