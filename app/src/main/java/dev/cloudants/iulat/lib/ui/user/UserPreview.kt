package dev.cloudants.iulat.lib.ui.user

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

data class UserSample(
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val address: String?,
    val mobileNumber: String?,
    val dateOfBirth: String,
    val email: String
)

@Composable
fun UserPreview(
    title: String,
    user: UserSample,
    onSave: suspend (UserSample) -> Unit,
    onCancel: () -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val isSaving = remember { mutableStateOf(false) }

    val userInfo = listOf(
        "First Name" to user.firstName,
        "Middle Name" to (user.middleName ?: ""),
        "Last Name" to user.lastName,
        "Address" to (user.address ?: ""),
        "Mobile Number" to (user.mobileNumber ?: ""),
        "Date of Birth" to formatDate(user.dateOfBirth),
        "Email" to user.email
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
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = title,
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        userInfo.forEach { (label, value) ->
            InfoRow(label, value)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Save button
        Button(
            onClick = {
                if (!isSaving.value) {
                    isSaving.value = true
                    coroutineScope.launch {
                        onSave(user)
                        isSaving.value = false
                    }
                }
            },
            enabled = !isSaving.value,
            modifier = Modifier
                .width(320.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF136204),
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

        // Cancel button
        TextButton(onClick = onCancel) {
            Text(
                text = "Cancel",
                fontSize = 15.sp,
                fontFamily = FontFamily.SansSerif
            )
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "$label:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value,
                fontSize = 17.sp,
                fontFamily = FontFamily.SansSerif
            )
        }
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
    }
}

fun formatDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "Select Date"
    return try {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val displayFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = isoFormat.parse(dateString)
        displayFormat.format(date ?: return "Select Date")
    } catch (e: Exception) {
        "Select Date"
    }
}
