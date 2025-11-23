package dev.cloudants.iulat.lib.ui.report

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 50.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "View Report ($reportTitle)",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start,
            color = Color.Black
        )
        UploadImageUI(
            title = "Evidence",
            existingBase64 = selectedReport,
            onImageSelected = { uri -> imageUri = uri }
        )
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            OutlinedTextField(
                value = textValue,
                onValueChange = { textValue = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .fillMaxWidth(),
            thickness = 1.dp,
            color = Color.Black
        )
        val timelineEventsMapped = timelineDto.mapIndexed { index, dto ->
            TimelineEvent(dto.time, dto.date, dto.message, isCurrent = index == timelineDto.lastIndex)
        }

        if (timelineEventsMapped.size >= 3) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                itemsIndexed(timelineEventsMapped) { index, event ->
                    TimelineItem(event = event, isLast = index == timelineEventsMapped.lastIndex)
                }
            }
        }

        LaunchedEffect(timelineEventsMapped.size) {
            if (timelineEventsMapped.isNotEmpty()) {
                listState.scrollToItem(timelineEventsMapped.lastIndex)
            }
        }

        if (timelineEventsMapped.size < 3) {
            OutlinedTextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .heightIn(min = 80.dp),
                placeholder = { Text("Add a new message") }
            )

            CustomButton(
                text = "Send",
                onClick = {
                    if (newMessage.isNotBlank() && timelineEventsMapped.size < 3) {
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
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun TimelineItem(event: TimelineEvent, isLast: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
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
            Canvas(modifier = Modifier.size(16.dp)) {
                drawCircle(
                    color = Color(0xFF007ACC),
                    radius = 6.dp.toPx(),
                    center = Offset(size.width / 2, 6.dp.toPx())
                )
            }
        }

        Card(
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
            border = BorderStroke(width = 1.dp, color = if (event.isCurrent) Color(0xFF007ACC) else Color.LightGray)
        ) {
            Text(text = event.message, modifier = Modifier.padding(10.dp), fontSize = 14.sp, color = Color.Black)
        }
    }
}
