package dev.cloudants.iulat.lib.ui.map
import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import dev.cloudants.iulat.R
import dev.cloudants.iulat.lib.models.entities.AddressDto

@Composable
fun LocationPickerModal(
    initialAddress: AddressDto?,
    onLocationSelected: (LatLng) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val startPoint = if (initialAddress != null && initialAddress.latitude != 0.0) {
        LatLng(initialAddress.latitude, initialAddress.longitude)
    } else {
        LatLng(12.458497, 120.963881)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startPoint, 17f)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getCurrentLocation(fusedLocationClient) { latLng ->
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true),
                    uiSettings = MapUiSettings(myLocationButtonEnabled = false)
                )

                Icon(
                    painter = painterResource(id = R.drawable.loc),
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(55.dp).align(Alignment.Center).padding(bottom = 24.dp)
                )

                FloatingActionButton(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        )
                    },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                    containerColor = Color.White
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.location_finder),
                        contentDescription = "Find Location",
                        tint = Color(0xFF0049AD),
                        modifier = Modifier.size(28.dp)
//                        0xFFD32F2F
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onLocationSelected(cameraPositionState.position.target)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !cameraPositionState.isMoving
                    ) {
                        Text(if (cameraPositionState.isMoving) "Locating..." else "Confirm Incident Location")
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

private fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (LatLng) -> Unit
) {
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(LatLng(location.latitude, location.longitude))
            }
        }
    } catch (e: SecurityException) {
        Log.e("LocationPicker", "Permission missing: ${e.message}")
    }
}