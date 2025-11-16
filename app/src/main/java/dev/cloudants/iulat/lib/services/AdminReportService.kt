package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.DashboardReportItemDto

interface AdminReportService {
    suspend fun getAllReports(): List<DashboardReportItemDto>
    suspend fun updateReportStatus(reportId: String, collectionName: String, newStatus: String)
    suspend fun getPendingReports(): List<DashboardReportItemDto>
}