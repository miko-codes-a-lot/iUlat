package dev.cloudants.iulat.lib.models.entities

import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    val id: String? = null,
    val province: String,
    val municipality: String,
    val barangay: String,
    val zone: String,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
)
