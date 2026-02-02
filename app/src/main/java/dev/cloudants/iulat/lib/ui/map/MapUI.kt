package dev.cloudants.iulat.lib.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import dev.cloudants.iulat.R
import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.maps.android.compose.*
import dev.cloudants.iulat.lib.components.context.MapReportData
import dev.cloudants.iulat.lib.utils.main.MainNav
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun MapUI(
    navController: NavController,
    reportData: MapReportData?
) {
    val context = LocalContext.current
    var customIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var locationName by remember { mutableStateOf("Loading address...") }
    LaunchedEffect(reportData) {
        if (reportData?.latitude != null && reportData.longitude != null) {
            MapsInitializer.initialize(context)
            customIcon = bitmapDescriptorFromVector(context, R.drawable.location_maker)

            withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(reportData.latitude!!, reportData.longitude!!, 1)
                    val result = addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown Location"

                    withContext(Dispatchers.Main) {
                        locationName = result
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        locationName = "Address Unavailable"
                    }
                }
            }
        }
    }

    if (reportData == null || reportData.latitude == null || reportData.longitude == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
            Text(text = "Loading report location...", modifier = Modifier.padding(top = 16.dp))
        }
        return
    }

    val targetLocation = LatLng(reportData.latitude!!, reportData.longitude!!)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(targetLocation, 16f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = MapType.NORMAL),
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            if (customIcon != null) {
                Marker(
                    state = rememberMarkerState(position = targetLocation).apply {
                        showInfoWindow()
                    },
                    title = locationName,
                    snippet = "Report : ${reportData.reportDetails?.take(60) ?: ""}${if ((reportData.reportDetails?.length ?: 0) > 60) "..." else ""}",
                    icon = customIcon,
                    anchor = Offset(0.5f, 0.5f)
                )
            }
        }

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
                        text = reportData.reportType?.uppercase() ?: "INCIDENT",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = Color.Red,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = locationName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = reportData.reportDetails ?: "No additional details provided.",
                    fontSize = 14.sp,
                    color = Color(0xFF42474E),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate(
                            MainNav.NotificationReportVIew(
                                title = reportData.reportType ?: "Others",
                                reportId = reportData.id ?: ""
                            )
                        )
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

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    val width = drawable.intrinsicWidth
    val height = drawable.intrinsicHeight

    drawable.setBounds(0, 0, width, height)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}