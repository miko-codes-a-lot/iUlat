package dev.cloudants.iulat.lib.models.entities

import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    val province: String,
    val municipality: String,
    val barangay: String,
)
