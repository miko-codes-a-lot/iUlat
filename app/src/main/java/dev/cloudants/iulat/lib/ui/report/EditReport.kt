package dev.cloudants.iulat.lib.ui.report

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun EditReport(
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
    reportId: String
) {
    val state by viewModel.state.collectAsState()
    var textValue by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val garbageState by garbageDisposalViewModel.state.collectAsState()
    val publicState by publicDisturbanceViewModel.state.collectAsState()
    val robberyState by robberiesViewModel.state.collectAsState()
    val brokenState by brokenStreetLightViewModel.state.collectAsState()
    val crashState by vehicleCrashViewModel.state.collectAsState()
    val roadState by roadRepairViewModel.state.collectAsState()
    val waterState by noWaterSupplyViewModel.state.collectAsState()
    val othersState by othersViewModel.state.collectAsState()

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
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.weight(1f))

        Text(
            text = "Report Status: Rejected ($reportTitle)",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start,
            color = Color.Black
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = { Text("Type feedback here", color = Color(0xFF0049AD)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 200.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "Upload a softcopy evidence",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start,
            color = Color.Black
        )
        UploadImageUI(
            title = "Tap to Upload Evidence",
            existingBase64 = selectedReport,
            onImageSelected = { uri -> imageUri = uri },
            enabled = false
        )


//        if (state.isDialogVisible) {
//            LoginDialog(
//                title = "Report Submitted",
//                label = "Success",
//                message = "Your report has been successfully submitted.",
//                isLoginSuccessful = true,
//                onConfirm = {
//                    viewModel.onIntent(ReportIntent.DismissDialog)
//                    navController.popBackStack()
//                },
//                onDismiss = {
//                    viewModel.onIntent(ReportIntent.DismissDialog)
//                }
//            )
//        }

        Spacer(Modifier.weight(1f))
//        CustomButton(
//            text = "Update",
//            onClick = {
//                if(reportTitle == MODULE.GARBAGE_DISPOSAL || reportTitle.equals("Garbage Disposal")) {
//                     val base64Image = imageUri?.let { uriToBase64(context, it) }
//                        ?: garbageState.selectedReport?.reportImage
//
//                    val garbage = GarbageDisposalDto(
//                        userId = currentUser.id!!,
//                        reportDetails = textValue,
//                        reportImage = base64Image,
//                        createdById = currentUser.id
//                    )
//                    garbageDisposalViewModel.createGarbageReport(garbage)
//
//                    viewModel.onIntent(ReportIntent.SubmitReport(reportContent = textValue))
//
//                }
//            }
//        )
//        Spacer(Modifier.weight(1f))
    }
}