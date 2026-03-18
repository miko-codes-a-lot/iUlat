package dev.cloudants.iulat.lib.ui.report

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import java.util.TimeZone
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CardDefaults.elevatedCardColors
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import dev.cloudants.iulat.lib.components.UploadVideoUI.UploadVideoUI
import dev.cloudants.iulat.lib.components.button.CustomButton
import dev.cloudants.iulat.lib.components.context.MODULE
import dev.cloudants.iulat.lib.components.context.uriToBase64
import dev.cloudants.iulat.lib.components.date.DatePickerDialogView
import dev.cloudants.iulat.lib.components.dialog.LoginDialog
import dev.cloudants.iulat.lib.components.upload_image.UploadImageUI
import dev.cloudants.iulat.lib.models.entities.AddressDto
import dev.cloudants.iulat.lib.models.entities.BrokenStreetlightsDto
import dev.cloudants.iulat.lib.models.entities.GarbageDisposalDto
import dev.cloudants.iulat.lib.models.entities.NoWaterSupplyDto
import dev.cloudants.iulat.lib.models.entities.OthersDto
import dev.cloudants.iulat.lib.models.entities.PublicDisturbanceDto
import dev.cloudants.iulat.lib.models.entities.RoadRepairDto
import dev.cloudants.iulat.lib.models.entities.RobberiesDto
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.models.entities.VehicleCrashDto
import dev.cloudants.iulat.lib.ui.map.LocationPickerModal
import dev.cloudants.iulat.lib.ui.report.intent.ReportIntent
import dev.cloudants.iulat.lib.viewmodels.AddressViewModel
import dev.cloudants.iulat.lib.viewmodels.BrokenStreetLightViewModel
import dev.cloudants.iulat.lib.viewmodels.GarbageDisposalViewModel
import dev.cloudants.iulat.lib.viewmodels.NoWaterSupplyViewModel
import dev.cloudants.iulat.lib.viewmodels.OthersViewModel
import dev.cloudants.iulat.lib.viewmodels.PublicDisturbanceViewModel
import dev.cloudants.iulat.lib.viewmodels.ReportViewModel
import dev.cloudants.iulat.lib.viewmodels.RoadRepairViewModel
import dev.cloudants.iulat.lib.viewmodels.RobberiesViewModel
import dev.cloudants.iulat.lib.viewmodels.VehicleCrashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    val scope = rememberCoroutineScope()
    var isSubmitting by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var showMapPicker by remember { mutableStateOf(false) }
    val addressViewModel: AddressViewModel = hiltViewModel()
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val displayFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    var selectedDateString by remember { mutableStateOf(dateFormat.format(calendar.time)) }
    var displayDate by remember { mutableStateOf(displayFormat.format(calendar.time)) }
    var userHomeAddress by remember { mutableStateOf<AddressDto?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    DatePickerDialogView(
        showDialog = showDatePicker,
        initialDateMillis = calendar.timeInMillis,
        onDismiss = { showDatePicker = false },
        onDateSelected = { utcMillis ->
            val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utcCalendar.timeInMillis = utcMillis
            val year = utcCalendar.get(Calendar.YEAR)
            val month = utcCalendar.get(Calendar.MONTH)
            val day = utcCalendar.get(Calendar.DAY_OF_MONTH)

            val localCalendar = Calendar.getInstance()
            localCalendar.set(year, month, day)
            selectedDateString = dateFormat.format(localCalendar.time)
            displayDate = displayFormat.format(localCalendar.time)
        }
    )

    LaunchedEffect(currentUser.id) {
        val addressId = currentUser.address?.id ?: ""
        userHomeAddress = addressViewModel.getAddressById(addressId)
    }

    if (showMapPicker) {
        LocationPickerModal(
            initialAddress = userHomeAddress,
            onLocationSelected = { latLng ->
                selectedLocation = latLng
                showMapPicker = false
            },
            onDismiss = { showMapPicker = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 50.dp, bottom = 50.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Create $reportTitle Report",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color(0xFF1A1C1E),
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }

            }

            item {
                EvidenceCard(title = "Issue Details") {
                    OutlinedTextField(
                        value = textValue,
                        onValueChange = { textValue = it },
                        placeholder = { Text("Describe the situation here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0049AD),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            item {
                EvidenceCard(title = "Upload Image Evidence") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            UploadImageUI(
                                title = "Photo",
                                onImageSelected = { uri -> imageUri = uri }
                            )
                        }
                    }
                }
            }

            item {
                EvidenceCard(title = "Upload Video Evidence") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            UploadVideoUI(
                                title = "Video",
                                onVideoSelected = { uri -> videoUri = uri },
                                selectedUri = videoUri
                            )
                        }
                    }
                }
            }

            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = elevatedCardColors(containerColor = Color.White),
                    elevation = elevatedCardElevation(2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = Color(0xFF2568EF),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Date of Incident",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF2568EF)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        CustomButton(
                            text = displayDate,
                            backgroundColor = Color(0xFF0049AD),
                            onClick = { showDatePicker = true },
                            height = 50f
                        )
                    }
                }
            }

            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = elevatedCardColors(containerColor = Color.White),
                    elevation = elevatedCardElevation(2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = Color(0xFF2568EF), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Location of Incident", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF2568EF))
                        }
                        Spacer(Modifier.height(12.dp))
                        CustomButton(
                            text = if (selectedLocation == null) "Pin Location" else "Location Pinned ✓",
                            backgroundColor = if (selectedLocation == null) Color(0xFF0049AD) else Color(0xFF4CAF50),
                            onClick = { showMapPicker = true },
                            height = 50f
                        )
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .padding(top = 20.dp, bottom = 20.dp)
                        .fillMaxWidth()
                ) {
                    CustomButton(
                        text = if (isSubmitting) "Submitting..." else "Submit Report",
                        onClick = {
                            if (selectedLocation == null) {
                                Toast.makeText(context, "Please pin the location first!", Toast.LENGTH_SHORT).show()
                                return@CustomButton
                            }
                            if (textValue.isBlank()) {
                                Toast.makeText(context, "Please enter details.", Toast.LENGTH_SHORT).show()
                                return@CustomButton
                            }
                            scope.launch {
                                isSubmitting = true
                                val base64Image = imageUri?.let { uriToBase64(context, it) }
                                val userId = currentUser.id ?: return@launch
                                val lat = selectedLocation?.latitude
                                val lng = selectedLocation?.longitude
                                val base64Video = videoUri?.let { uriToBase64(context, it) }
                                when (reportTitle) {
                                    MODULE.GARBAGE_DISPOSAL, "Garbage Disposal" -> {
                                        garbageDisposalViewModel.createGarbageReport(
                                            GarbageDisposalDto(userId = userId, email = currentUser.email, mobileNumber = currentUser.mobileNumber, reportDetails = textValue, reportImage = base64Image, reportVideo = base64Video, createdById = userId, latitude = lat, longitude = lng, createdAt = selectedDateString)
                                        )
                                    }
                                    MODULE.PUBLIC_DISTURBANCE, "Public Disturbance" -> {
                                        publicDisturbanceViewModel.createPublicDisturbanceReport(
                                            PublicDisturbanceDto(userId = userId, reportDetails = textValue, reportImage = base64Image, reportVideo = base64Video, createdById = userId, latitude = lat, longitude = lng, createdAt = selectedDateString)
                                        )
                                    }
                                    MODULE.ROBBERIES, "Robberies" -> {
                                        robberiesViewModel.createRobberiesReport(
                                            RobberiesDto(userId = userId, reportDetails = textValue, reportImage = base64Image, reportVideo = base64Video, createdById = userId, latitude = lat, longitude = lng, createdAt = selectedDateString)
                                        )
                                    }
                                    MODULE.BROKEN_STREETLIGHTS, "Broken Streetlights" -> {
                                        brokenStreetLightViewModel.createBrokenLightReport(
                                            BrokenStreetlightsDto(userId = userId, reportDetails = textValue, reportImage = base64Image, reportVideo = base64Video, createdById = userId, latitude = lat, longitude = lng, createdAt = selectedDateString)
                                        )
                                    }
                                    MODULE.VEHICLE_CRASH, "Vehicle Crashes" -> {
                                        vehicleCrashViewModel.createVehicleReport(
                                            VehicleCrashDto(userId = userId, reportDetails = textValue, reportImage = base64Image, reportVideo = base64Video, createdById = userId, latitude = lat, longitude = lng, createdAt = selectedDateString)
                                        )
                                    }
                                    MODULE.ROAD_REPAIR, "Road Repair" -> {
                                        roadRepairViewModel.createRoadRepairReport(
                                            RoadRepairDto(userId = userId, reportDetails = textValue, reportImage = base64Image, reportVideo = base64Video, createdById = userId, latitude = lat, longitude = lng, createdAt = selectedDateString)
                                        )
                                    }
                                    MODULE.NO_WATER_SUPPLY, "No Water Supply" -> {
                                        noWaterSupplyViewModel.createNoWaterSupplyReport(
                                            NoWaterSupplyDto(userId = userId, reportDetails = textValue, reportImage = base64Image, reportVideo = base64Video, createdById = userId, latitude = lat, longitude = lng, createdAt = selectedDateString)
                                        )
                                    }
                                    MODULE.OTHERS, "Others" -> {
                                        othersViewModel.createOthersReport(
                                            OthersDto(userId = userId, reportDetails = textValue, reportImage = base64Image, reportVideo = base64Video, createdById = userId, latitude = lat, longitude = lng, createdAt = selectedDateString)
                                        )
                                    }
                                }

                                delay(3000)
                                viewModel.onIntent(ReportIntent.SubmitReport(reportContent = textValue))
                            }
                        }
                    )
                }
            }
        }
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