package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.AddressDto
import dev.cloudants.iulat.lib.models.entities.DashboardReportItemDto
import dev.cloudants.iulat.lib.models.entities.TimelineEventDto

interface AdminReportService {
    suspend fun getAllReports(): List<DashboardReportItemDto>
    suspend fun updateReportStatus(reportId: String, collectionName: String, newStatus: String)
    suspend fun getPendingReports(): List<DashboardReportItemDto>
    suspend fun getReportsByStatus(status: String, search: String = ""): List<DashboardReportItemDto>
    suspend fun getTimelineEvents(reportId: String): List<TimelineEventDto>
    suspend fun saveTimelineMessage(timelineEvent: TimelineEventDto)
    suspend fun deleteReportsWithNoStatus()
    suspend fun getReportPercentages(): Map<String, Float>
    suspend fun getReportCounts(): Map<String, Int>
}