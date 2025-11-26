package dev.cloudants.iulat.lib.models.entities

import kotlinx.serialization.Serializable

@Serializable
data class MyLogsDto(
    var id: String? = null,
    var dateTimestamp: String? = ""
)
