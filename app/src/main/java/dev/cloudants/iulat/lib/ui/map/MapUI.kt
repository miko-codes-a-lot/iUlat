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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.*
import dev.cloudants.iulat.lib.components.context.MapReportData
import java.util.Locale

@Composable
fun MapUI(reportData: MapReportData?) {
    val context = LocalContext.current
    var customIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var locationName by remember { mutableStateOf("Loading address...") }

    LaunchedEffect(reportData) {
        if (reportData?.latitude != null && reportData.longitude != null) {
            MapsInitializer.initialize(context)
            customIcon = bitmapDescriptorFromVector(context, R.drawable.location_maker)

            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(reportData.latitude!!, reportData.longitude!!, 1)
                locationName = addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown Location"
            } catch (e: Exception) {
                locationName = "Address Unavailable"
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
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Reported Location",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = locationName,
                    style = MaterialTheme.typography.bodyMedium
                )
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