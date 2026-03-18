package dev.cloudants.iulat.lib.ui.user

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dev.cloudants.iulat.lib.models.entities.UserDto
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource

@Composable
fun UserPreview(
    title: String,
    user: UserDto,
    onSave: suspend (UserDto) -> Unit,
    onCancel: () -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val isSaving = remember { mutableStateOf(false) }

    val fullAddress = listOfNotNull(
        user.address?.zone.takeIf { it!!.isNotBlank() },
        user.address?.barangay.takeIf { it!!.isNotBlank() },
//        user.address?.municipality.takeIf { it!!.isNotBlank() },
    ).joinToString(", ")

    val userInfo = listOf(
        "First Name" to user.firstName,
        "Middle Name" to (user.middleName ?: ""),
        "Last Name" to user.lastName,
        "Gender" to user.gender,
        "Role" to when {
            user.isAdmin -> "Admin"
            user.isResidence -> "Residence"
            else -> "User"
        },
        "Address" to fullAddress,
        "Mobile Number" to (user.mobileNumber ?: ""),
        "Date of Birth" to formatDate(user.dateOfBirth),
        "Email" to user.email,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (user.isVerified) Color(0xFFE8F0FE) else Color(0xFFFFEBEE),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (user.isVerified) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (user.isVerified) Color(0xFF0049AD) else Color(0xFFD32F2F),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (user.isVerified) "ACCOUNT VERIFIED" else "NOT VERIFIED ACCOUNT ",
                    color = if (user.isVerified) Color(0xFF0049AD) else Color(0xFFD32F2F),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        userInfo.forEach { (label, value) ->
            InfoRow(label, value)
        }
        user.validId?.let { validId ->
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Uploaded ID:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(8.dp))

            val imageBitmap = remember(validId) {
                if (!validId.startsWith("http") && !validId.startsWith("content://")) {
                    try {
                        val bytes = Base64.decode(validId, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                    } catch (e: Exception) {
                        Log.e("UserPreview", "Failed to decode Base64 image: ${e.message}")
                        null
                    }
                } else null
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEFEFEF)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    imageBitmap != null -> Image(
                        bitmap = imageBitmap,
                        contentDescription = "User Valid ID",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    validId.startsWith("http") || validId.startsWith("content://") -> AsyncImage(
                        model = validId,
                        contentDescription = "User Valid ID",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    else -> Text("No valid image", color = Color.Gray)
                }
            }
        }

        user.voterCertificate?.let { voterCert ->
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Uploaded Voter Certificate:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(8.dp))

            val voterCertBitmap = remember(voterCert) {
                if (!voterCert.startsWith("http") && !voterCert.startsWith("content://")) {
                    try {
                        val bytes = Base64.decode(voterCert, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                    } catch (e: Exception) {
                        Log.e("UserPreview", "Failed to decode Voter Cert Base64: ${e.message}")
                        null
                    }
                } else null
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEFEFEF)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    voterCertBitmap != null -> Image(
                        bitmap = voterCertBitmap,
                        contentDescription = "User Voter Certificate",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    voterCert.startsWith("http") || voterCert.startsWith("content://") -> AsyncImage(
                        model = voterCert,
                        contentDescription = "User Voter Certificate",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    else -> Text("No valid image", color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (!isSaving.value) {
                    isSaving.value = true
                    coroutineScope.launch {
                        try {
                            onSave(user)
                        } finally {
                            isSaving.value = false
                        }
                    }
                }
            },
            enabled = !isSaving.value,
            modifier = Modifier
                .width(320.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0049AD),
                contentColor = Color.White
            )
        ) {
            Text(
                text = if (isSaving.value) "Saving..." else "Confirm",
                fontSize = 17.sp,
                fontFamily = FontFamily.SansSerif
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = onCancel) {
            Text(
                text = "Cancel",
                fontSize = 15.sp,
                fontFamily = FontFamily.SansSerif,
                color = Color(0xFF0049AD)
            )
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    val valueColor = when (value) {
        "Verified" -> Color(0xFF0049AD)
        "Not Verified" -> Color.Red
        else -> Color.Black
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value.ifBlank { "N/A" },
                fontSize = 17.sp,
                color = valueColor,
                fontFamily = FontFamily.SansSerif
            )
        }
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
    }
}

fun formatDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "Select Date"
    val displayFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    val possibleFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd"
    )

    for (pattern in possibleFormats) {
        try {
            val parser = SimpleDateFormat(pattern, Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val parsed = parser.parse(dateString)
            if (parsed != null) {
                return displayFormat.format(parsed)
            }
        } catch (_: Exception) {}
    }

    return "Select Date"
}

