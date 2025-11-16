package dev.cloudants.iulat.lib.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cloudants.iulat.lib.models.entities.DashboardReportItemDto
import dev.cloudants.iulat.lib.services.AdminReportService
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        loadReports()
    }

    fun loadReports() {
        viewModelScope.launch {
            val result: List<DashboardReportItemDto> = reportService.getAllReports()
            _reports.value = result
        }
    }

    fun updateReportStatus(report: DashboardReportItemDto, newStatus: String) {
        viewModelScope.launch {
            try {
                val collectionName = when (report.reportType) {
                    "Garbage Disposal" -> "garbage_disposal"
                    "Broken Streetlights" -> "broken_streetlights"
                    "No Water Supply" -> "no_water_supply"
                    "Others" -> "others"
                    "Public Disturbance" -> "public_disturbance"
                    "Road Repair" -> "road_repair"
                    "Robberies" -> "robberies"
                    "Vehicle Crash" -> "vehicle_crash"
                    else -> return@launch
                }
                reportService.updateReportStatus(report.reportId, collectionName, newStatus)
                loadReports()
            } catch (e: Exception) {
                Log.e("FAILED UPDATE : ", e.message.toString())
            }
        }
    }

    fun getAllPendingReports() {
        viewModelScope.launch {
            val pendingReports = reportService.getPendingReports()
            _reports.value = pendingReports
        }
    }


}