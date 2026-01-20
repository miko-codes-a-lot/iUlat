package dev.cloudants.iulat.lib.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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

@Composable
fun NotificationReportVIew(
    navController: NavController,
    reportTitle: String,
    viewModel: ReportViewModel,
    currentUser: UserDto,
    garbageDisposalViewModel: GarbageDisposalViewModel,
    publicDisturbanceViewModel: PublicDisturbanceViewModel,
    robberiesViewModel: RobberiesViewModel,
    brokenStreetLightViewModel: BrokenStreetLightViewModel,
    vehicleCrashViewModel: VehicleCrashViewModel,
    roadRepairViewModel: RoadRepairViewModel,
    noWaterSupplyViewModel: NoWaterSupplyViewModel,
    othersViewModel: OthersViewModel,
    reportId: String,
    adminViewModel: AdminReportViewModel,
) {
    var textValue by remember { mutableStateOf("") }
    var newMessage by remember { mutableStateOf("") }
    val garbageState by garbageDisposalViewModel.state.collectAsState()
    val publicState by publicDisturbanceViewModel.state.collectAsState()
    val robberyState by robberiesViewModel.state.collectAsState()
    val brokenState by brokenStreetLightViewModel.state.collectAsState()
    val crashState by vehicleCrashViewModel.state.collectAsState()
    val roadState by roadRepairViewModel.state.collectAsState()
    val waterState by noWaterSupplyViewModel.state.collectAsState()
    val othersState by othersViewModel.state.collectAsState()
    val timelineDto by adminViewModel.timeline.collectAsState()

    val selectedReport = when (reportTitle) {
        MODULE.GARBAGE_DISPOSAL, "Garbage Disposal" -> garbageState.selectedReport?.reportImage
        MODULE.PUBLIC_DISTURBANCE, "Public Disturbance" -> publicState.selectedReport?.reportImage
        MODULE.ROBBERIES, "Robberies" -> robberyState.selectedReport?.reportImage
        MODULE.BROKEN_STREETLIGHTS, "Broken Streetlights" -> brokenState.selectedReport?.reportImage
        MODULE.VEHICLE_CRASH, "Vehicle Crashes", "Vehicle Crash" -> crashState.selectedReport?.reportImage
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
            MODULE.VEHICLE_CRASH, "Vehicle Crashes", "Vehicle Crash" -> {
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
//        Spacer(modifier = Modifier.weight(1f))
        androidx.compose.material3.ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = androidx.compose.material3.CardDefaults.elevatedCardColors(
                containerColor = Color.White
            ),
            elevation = androidx.compose.material3.CardDefaults.elevatedCardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Report: $reportTitle",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF212121)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    UploadImageUI(
                        title = "Evidence Photo",
                        existingBase64 = selectedReport,
                        onImageSelected = { },
                        enabled = false
                    )
                }

                Text(
                    text = "Description",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                )

                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF9F9F9),
                        unfocusedContainerColor = Color(0xFFF9F9F9),
                        focusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedBorderColor = Color(0xFFEEEEEE),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }
        }
    }
}

