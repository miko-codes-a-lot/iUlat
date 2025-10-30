package dev.cloudants.iulat.lib.ui.user

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.cloudants.iulat.R

import kotlinx.coroutines.launch
import java.io.File

@Composable
fun UserProfile() {
    val coroutineScope = rememberCoroutineScope()
    var selectedImgUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

//    LaunchedEffect(currentUserId) {
//        val userDto = userService.fetchOne(currentUserId)
//        if (!userDto.userProfile.isNullOrEmpty()) {
//            val byteArray = android.util.Base64.decode(userDto.userProfile, android.util.Base64.DEFAULT)
//            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//            selectedImgUri = saveBitmapToUri(context, bitmap)
//        } else {
//            Log.e("UserImageUI", "Image Base64 is null or empty for user: $currentUserId")
//        }
//    }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedImgUri = uri
//                onImageSelected(uri)
                coroutineScope.launch {
//                    val byteArray = getBytesFromUri(context, uri)
//                    if (byteArray != null) {
//                        val result = userService.saveImage(currentUserId, byteArray)
//                        if (result.isSuccess) {
//                            delay(500)
//                            val userDto = userService.fetchOne(currentUserId)
//                            if (userDto.userProfile.isNullOrEmpty()) {
//                            } else {
//                                val byteArray = android.util.Base64.decode(userDto.userProfile, android.util.Base64.DEFAULT)
//                                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//                                selectedImgUri = saveBitmapToUri(context, bitmap)
//                            }
//                        } else {
//                            Log.e("UserImageUI", "Failed to save profile picture")
//                        }
//                    }
                }
            }
        }
    )

    Box(
        Modifier.height(140.dp)
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Color(0xFF0049AD))
                .border(3.dp,  Color(0xFF0049AD), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImgUri != null) {
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(140.dp)
                        .clip(CircleShape),
                    model = selectedImgUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.person),
                    contentDescription = "Default placeholder",
                    modifier = Modifier
                        .size(90.dp),
                    tint = Color.White
                )
            }
        }
        IconButton(
            onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier
                .size(35.dp)
                .padding(2.dp)
                .clip(CircleShape)
                .align(Alignment.BottomEnd),
            colors = IconButtonDefaults.iconButtonColors( Color(0xFF0049AD)),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.cameraalt),
                contentDescription = "Select Profile",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
                tint = Color.White
            )
        }
    }
}

fun getBytesFromUri(context: Context, uri: Uri): ByteArray? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use { it.readBytes() }
    } catch (e: Exception) {
        null
    }
}

fun saveBitmapToUri(context: Context, bitmap: Bitmap?, maxWidth: Int = 500, maxHeight: Int = 500): Uri? {
    val resizedBitmap = bitmap?.let { resizeBitmap(it, maxWidth, maxHeight) }

    val file = File(context.cacheDir, "profile_image.png")
    file.outputStream().use { out ->
        resizedBitmap?.compress(Bitmap.CompressFormat.PNG, 80, out)
        out.flush()
    }
    return Uri.fromFile(file)
}

fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
    val width: Int
    val height: Int
    if (bitmap.width > bitmap.height) {
        width = maxWidth
        height = (maxWidth / aspectRatio).toInt()
    } else {
        height = maxHeight
        width = (maxHeight * aspectRatio).toInt()
    }
    return Bitmap.createScaledBitmap(bitmap, width, height, true)
}

