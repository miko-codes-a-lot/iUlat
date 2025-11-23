package dev.cloudants.iulat.lib.models.entities

data class DashboardReportItemDto(
    val docId: String,
    val reportId: String,
    val reportType: String,
    val reportDetails: String,
    val reportDate: String,
    val status: String,
    val userName: String,
    val userEmail: String,
    val addressId: String
)