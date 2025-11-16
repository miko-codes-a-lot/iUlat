package dev.cloudants.iulat.lib.ui.report

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.cloudants.iulat.lib.components.button.CustomButton
import dev.cloudants.iulat.lib.components.context.MODULE
import dev.cloudants.iulat.lib.components.context.uriToBase64
import dev.cloudants.iulat.lib.components.dialog.LoginDialog
import dev.cloudants.iulat.lib.components.upload_image.UploadImageUI
import dev.cloudants.iulat.lib.models.entities.BrokenStreetlightsDto
import dev.cloudants.iulat.lib.models.entities.GarbageDisposalDto
import dev.cloudants.iulat.lib.models.entities.NoWaterSupplyDto
import dev.cloudants.iulat.lib.models.entities.OthersDto
import dev.cloudants.iulat.lib.models.entities.PublicDisturbanceDto
import dev.cloudants.iulat.lib.models.entities.RoadRepairDto
import dev.cloudants.iulat.lib.models.entities.RobberiesDto
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.models.entities.VehicleCrashDto
import dev.cloudants.iulat.lib.ui.report.intent.ReportIntent
import dev.cloudants.iulat.lib.viewmodels.BrokenStreetLightViewModel
import dev.cloudants.iulat.lib.viewmodels.GarbageDisposalViewModel
import dev.cloudants.iulat.lib.viewmodels.NoWaterSupplyViewModel
import dev.cloudants.iulat.lib.viewmodels.OthersViewModel
import dev.cloudants.iulat.lib.viewmodels.PublicDisturbanceViewModel
import dev.cloudants.iulat.lib.viewmodels.ReportViewModel
import dev.cloudants.iulat.lib.viewmodels.RoadRepairViewModel
import dev.cloudants.iulat.lib.viewmodels.RobberiesViewModel
import dev.cloudants.iulat.lib.viewmodels.VehicleCrashViewModel

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateReportPrev() {
    CreateReport(
        navController = rememberNavController(),
        reportTitle = "SAMPLE ",
        viewModel = viewModel(),
        currentUser = UserDto(),
        garbageDisposalViewModel = viewModel(),
        publicDisturbanceViewModel = viewModel(),
        robberiesViewModel = viewModel(),
        brokenStreetLightViewModel = viewModel(),
        vehicleCrashViewModel = viewModel(),
        roadRepairViewModel = viewModel(),
        noWaterSupplyViewModel = viewModel(),
        othersViewModel = viewModel(),
    )
}

@Composable
fun CreateReport(
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
) {
    val state by viewModel.state.collectAsState()
    var textValue by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.weight(1f))

        Text(
            text = "Issue Details ($reportTitle)",
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
        val imagePicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            imageUri = uri
        }
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
            existingBase64 = null,
            onImageSelected = { uri -> imageUri = uri }
        )

        Spacer(Modifier.weight(1f))
        Log.e("CURRENT USER ", currentUser.email)
        Log.e("REPORT TITLE ", reportTitle)
        CustomButton(
            text = "Submit",
            onClick = {
                if (textValue.isBlank()) return@CustomButton
                val base64Image = imageUri?.let { uriToBase64(context, it) }
                val userId = currentUser.id ?: return@CustomButton
                var reportCreated = false
                when (reportTitle) {
                    MODULE.GARBAGE_DISPOSAL, "Garbage Disposal" -> {
                        garbageDisposalViewModel.createGarbageReport(
                            GarbageDisposalDto(
                                userId = userId,
                                reportDetails = textValue,
                                reportImage = base64Image,
                                createdById = userId
                            )
                        )
                        reportCreated = true
                    }

                    MODULE.PUBLIC_DISTURBANCE, "Public Disturbance" -> {
                        publicDisturbanceViewModel.createPublicDisturbanceReport(
                            PublicDisturbanceDto(
                                userId = userId,
                                reportDetails = textValue,
                                reportImage = base64Image,
                                createdById = userId
                            )
                        )
                        reportCreated = true
                    }

                    MODULE.ROBBERIES, "Robberies" -> {
                        robberiesViewModel.createRobberiesReport(
                            RobberiesDto(
                                userId = userId,
                                reportDetails = textValue,
                                reportImage = base64Image,
                                createdById = userId
                            )
                        )
                        reportCreated = true
                    }

                    MODULE.BROKEN_STREETLIGHTS, "Broken Streetlights" -> {
                        brokenStreetLightViewModel.createBrokenLightReport(
                            BrokenStreetlightsDto(
                                userId = userId,
                                reportDetails = textValue,
                                reportImage = base64Image,
                                createdById = userId
                            )
                        )
                        reportCreated = true
                    }

                    MODULE.VEHICLE_CRASHES, "Vehicle Crashes" -> {
                        vehicleCrashViewModel.createVehicleReport(
                            VehicleCrashDto(
                                userId = userId,
                                reportDetails = textValue,
                                reportImage = base64Image,
                                createdById = userId
                            )
                        )
                        reportCreated = true
                    }

                    MODULE.ROAD_REPAIR, "Road Repair" -> {
                        roadRepairViewModel.createRoadRepairReport(
                            RoadRepairDto(
                                userId = userId,
                                reportDetails = textValue,
                                reportImage = base64Image,
                                createdById = userId
                            )
                        )
                        reportCreated = true
                    }

                    MODULE.NO_WATER_SUPPLY, "No Water Supply" -> {
                        noWaterSupplyViewModel.createNoWaterSupplyReport(
                            NoWaterSupplyDto(
                                userId = userId,
                                reportDetails = textValue,
                                reportImage = base64Image,
                                createdById = userId
                            )
                        )
                        reportCreated = true
                    }

                    MODULE.OTHERS, "Others" -> {
                        othersViewModel.createOthersReport(
                            OthersDto(
                                userId = userId,
                                reportDetails = textValue,
                                reportImage = base64Image,
                                createdById = userId
                            )
                        )
                        reportCreated = true
                    }
                }

                if (reportCreated) {
                    viewModel.onIntent(ReportIntent.SubmitReport(reportContent = textValue))
                }            }
        )
        Spacer(Modifier.weight(1f))
    }
    if (state.isDialogVisible) {
        LoginDialog(
            title = "Report Submitted",
            label = "Success",
            message = "Your report has been successfully submitted.",
            isLoginSuccessful = true,
            onConfirm = {
                viewModel.onIntent(ReportIntent.DismissDialog)
                navController.popBackStack()
            },
            onDismiss = {
                viewModel.onIntent(ReportIntent.DismissDialog)
            }
        )
    }
}
