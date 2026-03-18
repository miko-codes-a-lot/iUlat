package dev.cloudants.iulat.lib.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.cloudants.iulat.lib.components.VideoPlayerUI.VideoPlayerUI
import dev.cloudants.iulat.lib.components.context.MODULE
import dev.cloudants.iulat.lib.components.context.MapReportData
import dev.cloudants.iulat.lib.components.upload_image.UploadImageUI
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.ui.map.MapUI
import dev.cloudants.iulat.lib.viewmodels.AdminReportViewModel
import dev.cloudants.iulat.lib.viewmodels.BrokenStreetLightViewModel
import dev.cloudants.iulat.lib.viewmodels.GarbageDisposalViewModel
import dev.cloudants.iulat.lib.viewmodels.NoWaterSupplyViewModel
import dev.cloudants.iulat.lib.viewmodels.OthersViewModel
import dev.cloudants.iulat.lib.viewmodels.PublicDisturbanceViewModel
import dev.cloudants.iulat.lib.viewmodels.ReportViewModel
import dev.cloudants.iulat.lib.viewmodels.RoadRepairViewModel
import dev.cloudants.iulat.lib.viewmodels.RobberiesViewModel
import dev.cloudants.iulat.lib.viewmodels.VehicleCrashViewModel

@Composable
fun ViewRejReport(
    navController: NavController,
    reportTitle: String,
    viewModel: ReportViewModel,
    currentUser: UserDto,
    garbageDisposalViewModel : GarbageDisposalViewModel,
    publicDisturbanceViewModel : PublicDisturbanceViewModel,
    robberiesViewModel : RobberiesViewModel,
    brokenStreetLightViewModel : BrokenStreetLightViewModel,
    vehicleCrashViewModel : VehicleCrashViewModel,
    roadRepairViewModel : RoadRepairViewModel,
    noWaterSupplyViewModel : NoWaterSupplyViewModel,
    othersViewModel : OthersViewModel,
    reportId: String,
    adminViewModel: AdminReportViewModel,
) {
    val state by viewModel.state.collectAsState()
    var textValue by remember { mutableStateOf("") }
    val garbageState by garbageDisposalViewModel.state.collectAsState()
    val publicState by publicDisturbanceViewModel.state.collectAsState()
    val robberyState by robberiesViewModel.state.collectAsState()
    val brokenState by brokenStreetLightViewModel.state.collectAsState()
    val crashState by vehicleCrashViewModel.state.collectAsState()
    val roadState by roadRepairViewModel.state.collectAsState()
    val waterState by noWaterSupplyViewModel.state.collectAsState()
    val othersState by othersViewModel.state.collectAsState()
    val timelineDto by adminViewModel.timeline.collectAsState()
    val listState = rememberLazyListState()

    val selectedReport = when (reportTitle) {
        MODULE.GARBAGE_DISPOSAL, "Garbage Disposal" -> garbageState.selectedReport?.reportImage
        MODULE.PUBLIC_DISTURBANCE, "Public Disturbance" -> publicState.selectedReport?.reportImage
        MODULE.ROBBERIES, "Robberies" -> robberyState.selectedReport?.reportImage
        MODULE.BROKEN_STREETLIGHTS, "Broken Streetlights" -> brokenState.selectedReport?.reportImage
        MODULE.VEHICLE_CRASH, "Vehicle Crashes" -> crashState.selectedReport?.reportImage
        MODULE.ROAD_REPAIR, "Road Repair" -> roadState.selectedReport?.reportImage
        MODULE.NO_WATER_SUPPLY, "No Water Supply" -> waterState.selectedReport?.reportImage
        MODULE.OTHERS, "Others" -> othersState.selectedReport?.reportImage
        else -> null
    }
    val selectedVideo = when (reportTitle) {
        MODULE.GARBAGE_DISPOSAL, "Garbage Disposal" -> garbageState.selectedReport?.reportVideo
        MODULE.PUBLIC_DISTURBANCE, "Public Disturbance" -> publicState.selectedReport?.reportVideo
        MODULE.ROBBERIES, "Robberies" -> robberyState.selectedReport?.reportVideo
        MODULE.BROKEN_STREETLIGHTS, "Broken Streetlights" -> brokenState.selectedReport?.reportVideo
        MODULE.VEHICLE_CRASH, "Vehicle Crashes", "Vehicle Crash" -> crashState.selectedReport?.reportVideo
        MODULE.ROAD_REPAIR, "Road Repair" -> roadState.selectedReport?.reportVideo
        MODULE.NO_WATER_SUPPLY, "No Water Supply" -> waterState.selectedReport?.reportVideo
        MODULE.OTHERS, "Others" -> othersState.selectedReport?.reportVideo
        else -> null
    }
    LaunchedEffect(reportId) {
        adminViewModel.loadTimeline(reportId)
    }

    LaunchedEffect(reportId) {
        when (reportTitle) {
            MODULE.GARBAGE_DISPOSAL, "Garbage Disposal" -> {
                garbageDisposalViewModel.fetchReportById(reportId)
                garbageDisposalViewModel.state.collect { state ->
                    textValue = state.selectedReport?.reportDetails ?: ""
                }
            }
            MODULE.PUBLIC_DISTURBANCE, "Public Disturbance" -> {
                publicDisturbanceViewModel.fetchReportById(reportId)
                publicDisturbanceViewModel.state.collect { state ->
                    textValue = state.selectedReport?.reportDetails ?: ""
                }
            }
            MODULE.ROBBERIES, "Robberies" -> {
                robberiesViewModel.fetchReportById(reportId)
                robberiesViewModel.state.collect { state ->
                    textValue = state.selectedReport?.reportDetails ?: ""
                }
            }
            MODULE.BROKEN_STREETLIGHTS, "Broken Streetlights" -> {
                brokenStreetLightViewModel.fetchReportById(reportId)
                brokenStreetLightViewModel.state.collect { state ->
                    textValue = state.selectedReport?.reportDetails ?: ""
                }
            }
            MODULE.VEHICLE_CRASH, "Vehicle Crashes" -> {
                vehicleCrashViewModel.fetchReportById(reportId)
                vehicleCrashViewModel.state.collect { state ->
                    textValue = state.selectedReport?.reportDetails ?: ""
                }
            }
            MODULE.ROAD_REPAIR, "Road Repair" -> {
                roadRepairViewModel.fetchReportById(reportId)
                roadRepairViewModel.state.collect { state ->
                    textValue = state.selectedReport?.reportDetails ?: ""
                }
            }
            MODULE.NO_WATER_SUPPLY, "No Water Supply" -> {
                noWaterSupplyViewModel.fetchReportById(reportId)
                noWaterSupplyViewModel.state.collect { state ->
                    textValue = state.selectedReport?.reportDetails ?: ""
                }
            }
            MODULE.OTHERS, "Others" -> {
                othersViewModel.fetchReportById(reportId)
                othersViewModel.state.collect { state ->
                    textValue = state.selectedReport?.reportDetails ?: ""
                }
            }
        }
    }
    val currentStatus = timelineDto.lastOrNull()?.status ?: "Pending"

    val lat = when (reportTitle) {
        MODULE.GARBAGE_DISPOSAL, "Garbage Disposal" -> garbageState.selectedReport?.latitude
        MODULE.PUBLIC_DISTURBANCE, "Public Disturbance" -> publicState.selectedReport?.latitude
        MODULE.ROBBERIES, "Robberies" -> robberyState.selectedReport?.latitude
        MODULE.BROKEN_STREETLIGHTS, "Broken Streetlights" -> brokenState.selectedReport?.latitude
        MODULE.VEHICLE_CRASH, "Vehicle Crashes" -> crashState.selectedReport?.latitude
        MODULE.ROAD_REPAIR, "Road Repair" -> roadState.selectedReport?.latitude
        MODULE.NO_WATER_SUPPLY, "No Water Supply" -> waterState.selectedReport?.latitude
        MODULE.OTHERS, "Others" -> othersState.selectedReport?.latitude
        else -> null
    }

    val lng = when (reportTitle) {
        MODULE.GARBAGE_DISPOSAL, "Garbage Disposal" -> garbageState.selectedReport?.longitude
        MODULE.PUBLIC_DISTURBANCE, "Public Disturbance" -> publicState.selectedReport?.longitude
        MODULE.ROBBERIES, "Robberies" -> robberyState.selectedReport?.longitude
        MODULE.BROKEN_STREETLIGHTS, "Broken Streetlights" -> brokenState.selectedReport?.longitude
        MODULE.VEHICLE_CRASH, "Vehicle Crashes" -> crashState.selectedReport?.longitude
        MODULE.ROAD_REPAIR, "Road Repair" -> roadState.selectedReport?.longitude
        MODULE.NO_WATER_SUPPLY, "No Water Supply" -> waterState.selectedReport?.longitude
        MODULE.OTHERS, "Others" -> othersState.selectedReport?.longitude
        else -> null
    }

    val mapReportData = if (lat != null && lng != null) {
        MapReportData(
            id = reportId,
            latitude = lat,
            longitude = lng,
            reportDetails = textValue,
            reportType = reportTitle
        )
    } else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
            .padding(top = 40.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = reportTitle,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = Color(0xFF1A1C1E)
                )
                Text(text = "Report Case ID: ${reportId.take(8)}...", color = Color.Gray, fontSize = 12.sp)
            }
            StatusChip(status = currentStatus)
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            state = listState
        ) {
            item {
                EvidenceCard(title = "Report Details") {
                    Text(
                        text = textValue.ifBlank { "No additional details provided." },
                        fontSize = 15.sp,
                        color = Color(0xFF42474E),
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                EvidenceCard(title = "Evidence Photo") {
                    UploadImageUI(
                        title = "",
                        existingBase64 = selectedReport,
                        onImageSelected = { },
                        enabled = false
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                EvidenceCard(title = "Evidence Video") {
                    if (!selectedVideo.isNullOrEmpty()) {
                        VideoPlayerUI(
                            videoUrl = selectedVideo,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        EmptyMediaPlaceholder("No video evidence attached.")
                    }
                }

            }


            item {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Incident Location",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF2568EF),
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp)
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    if (mapReportData != null) {
                        MapUI(
                            navController = navController,
                            reportData = mapReportData,
                            status = currentStatus
                        )
                    } else {
                        EmptyMediaPlaceholder("Location data is currently unavailable.")
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

@Composable
fun EvidenceCard(
    title: String,
    content: @Composable () -> Unit
) {
    androidx.compose.material3.ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF2568EF),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun EmptyMediaPlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Color(0xFFF1F3F4), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.Gray,
            fontSize = 13.sp,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}