package dev.cloudants.iulat.lib.models.entities

import dev.cloudants.iulat.lib.components.context.PrintableRow
import kotlinx.serialization.Serializable

@Serializable
data class GarbageDisposalDto(
    var id: String? = null,
    var userId: String = "",
    val addressId: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val email: String? = null,
    val mobileNumber: String? = null,
    var reportDetails: String = "",
    var reportImage: String? = null,
    var status: String = "Pending",
    var createdById: String? = null,
    var createdAt: String? = null,
    var reviewById: String? = null,
    var lastUpdatedById: String? = null,
    var lastUpdatedAt: String? = null,
    var deletedById: String? = null,
    var deletedAt: String? = null,

) : PrintableRow {

    override fun getColumns(): List<String> {
        return listOf(
            createdAt ?: "",
            status
        )
    }
}