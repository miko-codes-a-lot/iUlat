package dev.cloudants.iulat.lib.models.entities

data class TimelineEventDto(
    val docId: String,
    val userId: String,
    val status: String,
    val reportId: String,
    val time: String,
    val date: String,
    val message: String
)
