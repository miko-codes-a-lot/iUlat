package dev.cloudants.iulat.lib.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cloudants.iulat.lib.models.entities.DashboardReportItemDto
import dev.cloudants.iulat.lib.services.AdminReportService
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.models.entities.TimelineEventDto
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.services.NotificationService
import dev.cloudants.iulat.lib.services.UserService
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AdminReportViewModel @Inject constructor(
    private val reportService: AdminReportService,
    private val notificationService: NotificationService,
    private val userService: UserService
) : ViewModel() {

    private val _reports = MutableStateFlow<List<DashboardReportItemDto>>(emptyList())
    val reports: StateFlow<List<DashboardReportItemDto>> = _reports

    private val _recent_reports = MutableStateFlow<List<DashboardReportItemDto>>(emptyList())
    val recentReports: StateFlow<List<DashboardReportItemDto>> = _recent_reports

    private val _timeline = MutableStateFlow<List<TimelineEventDto>>(emptyList())
    val timeline: StateFlow<List<TimelineEventDto>> = _timeline

    private val _reportPercentages = MutableStateFlow<Map<String, Float>>(emptyMap())
    val reportPercentages: StateFlow<Map<String, Float>> = _reportPercentages

    private val _pieChartData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val pieChartData: StateFlow<Map<String, Int>> = _pieChartData

    init {
        loadReports()
        loadDashboardStats()
        loadRecentReports()
    }

    fun loadReportsByStatus(status: String, search: String) {
        viewModelScope.launch {
            try {
                val allReportsForStatus = reportService.getReportsByStatus(status, "")

                val filteredResult = if (search.isEmpty()) {
                    allReportsForStatus
                } else {
                    allReportsForStatus.filter { report ->
                        report.userName.contains(search, ignoreCase = true) ||
                                report.reportType.contains(search, ignoreCase = true)
                    }
                }

                _reports.value = filteredResult

                Log.d("SEARCH_DEBUG", "Found ${filteredResult.size} items for status: $status and query: $search")
            } catch (e: Exception) {
                Log.e("LOAD_REPORTS_ERROR", e.message.toString())
            }
        }
    }

    fun loadReports() {
        viewModelScope.launch {
            val result: List<DashboardReportItemDto> = reportService.getAllReports()
            _reports.value = result
            result.forEach { report ->
                Log.d("DATA_LOAD", "Report: ${report.reportType}, Status: ${report.status}, User: ${report.userName}, Address: ${report.addressId} ")
            }
        }
    }

    fun loadRecentReports() {
        viewModelScope.launch {
            val allReports = reportService.getAllReports()
            _recent_reports.value = allReports.take(4)
            allReports.take(5).forEach { report ->
                Log.d("RECENT_REPORT", "${report.reportType} - ${report.reportDate}")
            }
        }
    }


    fun updateReportStatus(report: DashboardReportItemDto, newStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val collectionName = when (report.reportType) {
                    "Garbage Disposal" -> "garbage_disposal"
                    "Broken Streetlights" -> "broken_streetlights"
                    "No Water Supply" -> "no_water_supply"
                    "Others" -> "others"
                    "Public Disturbance" -> "public_disturbance"
                    "Road Repair" -> "road_repair"
                    "Robberies" -> "robberies"
                    "Vehicle Crashes" -> "vehicle_crash"
                    else -> return@launch
                }
                reportService.updateReportStatus(report.docId, collectionName, newStatus)
                val emergencyTypes = listOf("Robberies", "Public Disturbance")
                val isEmergency = emergencyTypes.any { report.reportType.equals(it, ignoreCase = true) }

                val customMessage = if (isEmergency && newStatus == "Approve") {
                    "Your report has been approved. Help is on the way and should arrive in about 3 minutes. Please stay safe."
                } else {
                    "Report status updated to $newStatus"
                }

                createTimelineMessage(
                    reportId = report.docId,
                    userId = "Admin",
                    status = newStatus,
                    message = customMessage
                )
                loadReportsByStatus(newStatus, "")
                loadTimeline(report.docId)
            } catch (e: Exception) {
                Log.e("FAILED UPDATE : ", e.message.toString())
            }
        }
    }

    fun loadTimeline(reportId: String) {
        viewModelScope.launch {
            _timeline.value = reportService.getTimelineEvents(reportId)
        }
    }

    fun createTimelineMessage(reportId: String, userId: String, status: String, message: String) {
        val now = java.time.LocalDateTime.now()
        val time = now.format(java.time.format.DateTimeFormatter.ofPattern("hh:mma"))
        val date = now.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd"))

        val timelineEvent = TimelineEventDto(
            docId = "",
            userId = userId,
            status = status,
            reportId = reportId,
            time = time,
            date = date,
            message = message
        )

        viewModelScope.launch {
            reportService.saveTimelineMessage(timelineEvent)
            loadTimeline(reportId)
        }
    }
    init {
        viewModelScope.launch {
            reportService.deleteReportsWithNoStatus()
            loadReports()
        }
    }

    fun loadDashboardStats() {
        viewModelScope.launch {
            val counts = reportService.getReportCounts()
            _pieChartData.value = counts

            val totalReports = counts.values.sum().toFloat()
            val percentages = mutableMapOf<String, Float>()

            if (totalReports > 0) {
                for ((key, value) in counts) {
                    percentages[key] = (value.toFloat() / totalReports) * 100f
                }
            } else {
                counts.keys.forEach { key -> percentages[key] = 0f }
            }

            _reportPercentages.value = percentages
        }
    }

    fun sendAnnouncement(title: String, message: String, adminId: String) {
        viewModelScope.launch {
            notificationService.broadcastAnnouncement(adminId, title, message)
        }
    }
}