package dev.cloudants.iulat.lib.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cloudants.iulat.lib.models.entities.DashboardReportItemDto
import dev.cloudants.iulat.lib.services.AdminReportService
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.models.entities.TimelineEventDto
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AdminReportViewModel @Inject constructor(
    private val reportService: AdminReportService
) : ViewModel() {

    private val _reports = MutableStateFlow<List<DashboardReportItemDto>>(emptyList())
    val reports: StateFlow<List<DashboardReportItemDto>> = _reports

    private val _timeline = MutableStateFlow<List<TimelineEventDto>>(emptyList())
    val timeline: StateFlow<List<TimelineEventDto>> = _timeline
    init {
        loadReports()
    }

    fun loadReportsByStatus(status: String, search: String) {
        viewModelScope.launch {
            val result = reportService.getReportsByStatus(status, search)
            _reports.value = result
            result.forEach { report ->
                Log.d("REPORT_STATUS 1", "Report: ${report.reportType}, Status: ${report.status}, User: ${report.userName}, Address: ${report.addressId} ")
            }
        }
    }

    fun loadReports() {
        viewModelScope.launch {
            val result: List<DashboardReportItemDto> = reportService.getAllReports()
            _reports.value = result
            result.forEach { report ->
                Log.d("0", "Report: ${report.reportType}, Status: ${report.status}, User: ${report.userName}, Address: ${report.addressId} ")
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
                    "Public Disturbance" -> "public_dvgisturbance"
                    "Road Repair" -> "road_repair"
                    "Robberies" -> "robberies"
                    "Vehicle Crash" -> "vehicle_crash"
                    else -> return@launch
                }
                reportService.updateReportStatus(report.docId, collectionName, newStatus)
                loadReportsByStatus(report.status, "")
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

}