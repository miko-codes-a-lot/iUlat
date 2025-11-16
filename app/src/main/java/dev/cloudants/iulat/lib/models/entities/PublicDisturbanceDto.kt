package dev.cloudants.iulat.lib.models.entities

import kotlinx.serialization.Serializable

@Serializable
data class PublicDisturbanceDto(
    var id: String? = null,
    var userId: String = "",
    var reportDetails: String = "",
    var reportImage: String? = null,
    var status: String? = "pending",
    var createdById: String? = null,
    var createdAt: String? = null,
    var reviewById: String? = null,
    var lastUpdatedById: String? = null,
    var lastUpdatedAt: String? = null,
    var deletedById: String? = null,
    var deletedAt: String? = null,
)