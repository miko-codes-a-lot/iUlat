package dev.cloudants.iulat.lib.components.upload_image

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import coil.compose.AsyncImage

import android.app.DatePickerDialog
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.cloudants.iulat.R
import java.util.*

@Composable
fun UploadImageUI(
    title: String = "Tap to Upload",
    existingBase64: String? = null,
    existingUrl: String? = null,
    onImageSelected: (Uri?) -> Unit,
    enabled: Boolean = true,
) {
    var selectedUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val base64Bitmap = remember(existingBase64) {
        if (!existingBase64.isNullOrEmpty()) {
            try {
                val decoded = Base64.decode(existingBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedUri = uri
        onImageSelected(uri)
    }

    val displayImage: Any? = when {
        selectedUri != null -> selectedUri
        base64Bitmap != null -> base64Bitmap.asImageBitmap()
        existingUrl?.startsWith("http") == true -> existingUrl
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable(
                enabled = enabled,
                onClick = { pickerLauncher.launch("image/*") }
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (displayImage) {
                is ImageBitmap -> {
                    Image(
                        bitmap = displayImage,
                        contentDescription = "uploaded image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                is String, is Uri -> {
                    AsyncImage(
                        model = displayImage,
                        contentDescription = "uploaded image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                else -> {
                    Text(
                        text = title,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
