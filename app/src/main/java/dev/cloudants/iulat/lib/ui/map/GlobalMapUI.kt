package dev.cloudants.iulat.lib.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dev.cloudants.iulat.R
import dev.cloudants.iulat.lib.components.context.MapReportData
import dev.cloudants.iulat.lib.utils.main.MainNav
import java.util.Locale
@Composable
fun GlobalMapUI(
    navController: NavController,
    reportList: List<MapReportData>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var reportIcons by remember { mutableStateOf<Map<String, BitmapDescriptor?>>(emptyMap()) }
    var selectedReport by remember { mutableStateOf<MapReportData?>(null) }
    var selectedAddress by remember { mutableStateOf("Fetching address...") }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(12.458497, 120.963881), 16f)
    }

    LaunchedEffect(Unit) {
        com.google.android.gms.maps.MapsInitializer.initialize(context)
        val icons = mutableMapOf<String, BitmapDescriptor?>()
        val redColor = android.graphics.Color.RED
        val iconSize = 42

        val categories = listOf(
            "Garbage Disposal" to R.drawable.trash_can,
            "Public Disturbance" to R.drawable.ic_public_disturbance,
            "Robberies" to R.drawable.ic_robberies,
            "Broken Streetlights" to R.drawable.streetlight,
            "Vehicle Crashes" to R.drawable.ic_vehicle_crashes,
            "Road Repair" to R.drawable.road_work,
            "No Water Supply" to R.drawable.no_water,
            "Others" to R.drawable.ic_others
        )

        categories.forEach { (type, resId) ->
            icons[type] = bitmapDescriptorFromVectorGlobal(context, resId, redColor, iconSize)
        }
        icons["default"] = bitmapDescriptorFromVectorGlobal(context, R.drawable.location_maker, redColor, iconSize)
        reportIcons = icons
    }

    LaunchedEffect(selectedReport) {
        selectedReport?.let { data ->
            if (data.latitude != null && data.longitude != null) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(data.latitude, data.longitude, 1)
                    selectedAddress = addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown Address"
                } catch (e: Exception) {
                    selectedAddress = "Address Unavailable"
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (reportIcons.isNotEmpty()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = true)
            ) {
                reportList.forEach { report ->
                    if (report.latitude != null && report.longitude != null) {
                        val icon = reportIcons[report.reportType] ?: reportIcons["default"]
                        Marker(
                            state = MarkerState(position = LatLng(report.latitude, report.longitude)),
                            title = report.reportType ?: "Report",
                            icon = icon,
                            anchor = androidx.compose.ui.geometry.Offset(0.5f, 1.0f),
                            onClick = {
                                selectedReport = report
                                false
                            }
                        )
                    }
                }
            }
        }

        androidx.compose.material3.Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            color = Color.White.copy(alpha = 0.9f),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.IconButton(onClick = onBack) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF0049AD)
                    )
                }
                Column {
                    Text(
                        text = "Barangay Report Map",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color(0xFF0049AD)
                    )
                    Text(
                        text = "${reportList.size} Active Incidents Detected",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        if (selectedReport != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.Red, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedReport?.reportType?.uppercase() ?: "INCIDENT",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color.Red,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = selectedAddress,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = selectedReport?.reportDetails ?: "No additional details.",
                        fontSize = 14.sp,
                        color = Color(0xFF42474E),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            selectedReport?.let { report ->
                                navController.navigate(
                                    MainNav.NotificationReportVIew(
                                        title = report.reportType ?: "Others",
                                        reportId = report.id ?: ""
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0049AD)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("View Full Report Details")
                    }
                }
            }
        }
    }
}

fun bitmapDescriptorFromVectorGlobal(
    context: Context,
    vectorResId: Int,
    tintColor: Int? = null,
    sizeInDp: Int = 40
): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    val pxSize = (sizeInDp * context.resources.displayMetrics.density).toInt()
    val bitmap = Bitmap.createBitmap(pxSize, pxSize, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    tintColor?.let {
        androidx.core.graphics.drawable.DrawableCompat.setTint(drawable, it)
    }
    drawable.setBounds(0, 0, pxSize, pxSize)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}