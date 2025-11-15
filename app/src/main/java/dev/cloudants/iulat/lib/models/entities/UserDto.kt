package dev.cloudants.iulat.lib.models.entities

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String? = null,
    val username: String = "",
    var password: String = "",
    var firstName: String = "",
    var middleName: String? = null,
    var lastName: String = "",
    var email: String = "",
    var mobileNumber: String? = null,
    var dateOfBirth: String = "",
    var userProfile: String? = null,
    val gender: String = "",
    var validId: String? = null,
    val address: AddressDto? = null,
    val type: String? = null,
    var isAdmin: Boolean = false,
    var isResidence: Boolean = false,
    var createdById: String? = null,
    var createdAt: String? = null,
    var imageBase64: String? = null,
    var resetPasswordToken: String? = null,
    var resetTokenExpiration: String? = null,
    var isVerified: Boolean = false,
)
