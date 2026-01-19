package dev.cloudants.iulat.lib.ui.report

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.cloudants.iulat.lib.components.button.CustomButton
import dev.cloudants.iulat.lib.components.context.MODULE
import dev.cloudants.iulat.lib.components.upload_image.UploadImageUI
import dev.cloudants.iulat.lib.models.entities.UserDto
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

data class TimelineEvent(val time: String, val date: String, val message: String, val isCurrent: Boolean = false)

@Composable
fun ViewReport(
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
    var newMessage by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val garbageState by garbageDisposalViewModel.state.collectAsState()
    val publicState by publicDisturbanceViewModel.state.collectAsState()
    val robberyState by robberiesViewModel.state.collectAsState()
    val brokenState by brokenStreetLightViewModel.state.collectAsState()
    val crashState by vehicleCrashViewModel.state.collectAsState()
    val roadState by roadRepairViewModel.state.collectAsState()
    val waterState by noWaterSupplyViewModel.state.collectAsState()
    val othersState by othersViewModel.state.collectAsState()
    val timelineEvents by adminViewModel.timeline.collectAsState()
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

    val timelineEventsMapped = timelineDto.mapIndexed { index, dto ->
        TimelineEvent(dto.time, dto.date, dto.message, isCurrent = index == timelineDto.lastIndex)
    }
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        UploadImageUI(
                            title = "Evidence Image",
                            existingBase64 = selectedReport,
                            onImageSelected = { }
                        )

                        Text(
                            text = "Report Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                            color = Color(0xFF007ACC)
                        )

                        Text(
                            text = textValue.ifBlank { "No additional details provided." },
                            fontSize = 15.sp,
                            color = Color.DarkGray,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Updates & History",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            itemsIndexed(timelineEventsMapped) { index, event ->
                TimelineItem(event = event, isLast = index ==  timelineEventsMapped.lastIndex)
            }


        val shouldShowInput = !currentUser.isResidence &&
                timelineEventsMapped.size < 3 &&
                currentStatus != "Rejected"

        if (shouldShowInput) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        OutlinedTextField(
                            value = newMessage,
                            onValueChange = { newMessage = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Write a message...") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF1F3F4),
                                unfocusedContainerColor = Color(0xFFF1F3F4),
                                focusedBorderColor = Color(0xFF007ACC)
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        CustomButton(
                            text = "Send Update",
                            onClick = {
                                if (newMessage.isNotBlank()) {
                                    adminViewModel.createTimelineMessage(
                                        reportId = reportId,
                                        userId = currentUser.id!!,
                                        status = "Approve",
                                        message = newMessage
                                    )
                                    newMessage = ""
                                    adminViewModel.loadTimeline(reportId)
                                }
                            }
                        )
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when(status) {
        "Rejected" -> Color(0xFFE53935)
        "Approve", "Resolved" -> Color(0xFF43A047)
        else -> Color(0xFFFB8C00)
    }
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = status, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TimelineItem(event: TimelineEvent, isLast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.Top
    ) {

        Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = 8.dp)) {
            Text(event.time, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(event.date, fontSize = 12.sp, color = Color.Black)
        }

        Box(
            modifier = Modifier.width(24.dp).fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            Canvas(modifier = Modifier.width(2.dp).height(if (!isLast) 10.dp else 50.dp)) {
                drawLine(
                    color = Color.LightGray,
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = 2.dp.toPx()
                )
            }
            Canvas(
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                drawCircle(
                    color = Color(0xFF007ACC),
                    radius = 6.dp.toPx(),
                    center = Offset(size.width / 2, 6.dp.toPx())
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F3F4).copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = event.message,
                modifier = Modifier.padding(12.dp),
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 18.sp
            )
        }
    }
}
