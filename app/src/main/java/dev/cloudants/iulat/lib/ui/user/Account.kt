package dev.cloudants.iulat.lib.ui.user

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.cloudants.iulat.R
import coil.compose.rememberAsyncImagePainter
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.utils.main.MainNav
import dev.cloudants.iulat.lib.viewmodels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Account(navController: NavController, currentUser: UserDto) {
    val userViewModel: UserViewModel = hiltViewModel()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userState by userViewModel.currentUserState.collectAsState()
    androidx.compose.runtime.LaunchedEffect(currentUser.id) {
        if (currentUser.id != null) {
            userViewModel.loadCurrentUser(currentUser.id)
        }
    }
    val activeUser = userState ?: currentUser
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                coroutineScope.launch(Dispatchers.IO) {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val bytes = inputStream?.readBytes()
                    if (bytes != null && activeUser.id != null) {
                        userViewModel.updateProfileImage(activeUser.id, bytes)
                    }
                }
            }
        }
    )
    Row(
        modifier = Modifier
            .background(Color(0xFF0049AD))
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .height(175.dp)
                .background(Color(0xFF0049AD))
                .padding(16.dp)
                .wrapContentHeight()
        ) {
            Box(
                modifier = Modifier
                    .offset(y = (70).dp)
                    .align(Alignment.TopCenter)
                    .size(145.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFFFFF))
            ) {
                val imageSource = if (!activeUser.imageBase64.isNullOrEmpty()) {
                    val imageBytes = android.util.Base64.decode(activeUser.imageBase64, android.util.Base64.DEFAULT)
                    imageBytes
                } else {
                    activeUser.userProfile ?: "https://img.freepik.com/premium-vector/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-vector-illustration_561158-3467.jpg"
                }

                Image(
                    painter = rememberAsyncImagePainter(model = imageSource),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                        .fillMaxWidth()
                        .background(Color(0xFF0049AD))
                )
            }
            Surface(
                shape = CircleShape,
                color = Color.White,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(38.dp)
                    .offset(x = (15).dp, y = (-25).dp)
                    .border(1.dp, Color.LightGray, CircleShape)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.editicon),
                        contentDescription = "Edit Profile",
                        tint = Color(0xFF0049AD),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

//        Row(
//            modifier = Modifier
//                .padding(top = 10.dp)
//                .height(50.dp)
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.End
//        ){
//            Button(
//                onClick = {
//
//                },
//                modifier = Modifier,
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.White,
//                    contentColor = Color(0xFF136204)
//                )
//            ) {
////                    if (vm.isBusy) {
////                        CircularProgressIndicator(
////                            modifier = Modifier.size(26.dp),
////                            color = Color(0xFF136204),
////                        )
////                    } else {
//                    Icon(
//                        painter = painterResource(id = R.drawable.exit),
//                        contentDescription = "Logout",
//                        tint = Color.Red,
//                        modifier = Modifier.size(35.dp)
//                    )
////                    }
//            }
//        }
    }
//    Profile(navController = navController)
    Spacer(modifier = Modifier.padding(vertical = 50.dp))
    Column(
        modifier = Modifier
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        UserDetails(
            navController = navController,
            activeUser = activeUser,
            onIdSelected = { uri ->
                uri?.let {
                    coroutineScope.launch(Dispatchers.IO) {
                        val inputStream = context.contentResolver.openInputStream(it)
                        val bytes = inputStream?.readBytes()
                        if (bytes != null && activeUser.id != null) {
                            userViewModel.saveValidId(activeUser.id, bytes)
                        }
                    }
                }
            }
        )

        Spacer(modifier = Modifier.padding(vertical = 5.dp))

        Setting(navController, currentUser)
    }
}


@Composable
fun Profile(
    navController: NavController,
){
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedProfileImageUri by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        UserProfile(
            //            onImageSelected = { newUri ->
            //                selectedProfileImageUri = newUri
            //                coroutineScope.launch {
            //                    val byteArray = getBytesFromUri(context, newUri)
            //                    if (byteArray != null) {
            //                        userService.saveImage(currentUser.id!!, byteArray)
            //                    }
            //                }
            //            },
            //            currentUserId = currentUser.id!!,
            //            userService = userService
        )


    }
}

@Composable
fun UserDetails(
    navController: NavController,
    activeUser: UserDto,
    onIdSelected: (Uri?) -> Unit
) {
    var isUploadVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Contact Details",
                fontSize = 28.sp,
                fontFamily = FontFamily.SansSerif,
                color = Color(0xFF0049AD),
                modifier = Modifier.padding(horizontal = 3.dp)
            )
            if(activeUser.isVerified) {
                IconButton(
                    onClick = { isUploadVisible = !isUploadVisible }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "Toggle Upload ID",
                        tint = if (isUploadVisible) Color.Red else Color(0xFF0049AD),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        if (isUploadVisible) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "Update ID Document",
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    UploadIdUI(
                        existingImageUrl = activeUser.validId,
                        onImageSelected = { uri ->
                            onIdSelected(uri)
                        }
                    )
                }
            }
        }
//            }
//        }
//        if(isShowEditIcon.value) {
//            IconButton(
//                onClick = {
//                    navController.navigate("${MainNav.EditSettings}/fullName")
//                },
//                modifier = Modifier
//                    .size(30.dp)
//                    .padding(bottom = 3.dp)
//                    .clip(CircleShape),
//                colors = IconButtonDefaults.iconButtonColors(Color(0xFFFFFFFF)),
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.editicon),
//                    contentDescription = "Edit Details",
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(5.dp),
//                    tint = Color(0xFF136204)
//                )
//            }
//        }
    }
}

@Composable
fun Setting(navController: NavController, currentUser: UserDto){
    val mobileText =
        if (currentUser.mobileNumber.isNullOrEmpty()) {
            "N/A"
        } else {
            currentUser.mobileNumber
        }
    val settingsMenu = listOf(
        SettingItem(text = "${currentUser.email}"){
            navController.navigate("${MainNav.EditAccount}/email")
        },
        SettingItem(text = "${mobileText}"){
            navController.navigate("${MainNav.EditAccount}/mobileNumber")
        },
        SettingItem(text = "**************"){
            navController.navigate("${MainNav.EditAccount}/password")
        }
    )
    Column {
        settingsMenu.forEach { menuSettings ->
            Spacer(modifier = Modifier.height(10.dp))
            SettingButton(text = menuSettings.text, onClick = menuSettings.action)
        }
    }
}

@Composable
fun SettingButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(start = 16.dp, end = 16.dp)
            .border(2.dp, color = Color(0xFF0049AD), shape = RoundedCornerShape(5.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color(0xFF0049AD)
        )
    ) {
        Row(
            modifier = Modifier
        ){
            Text(text = text, fontSize = 16.sp, fontFamily = FontFamily.SansSerif)
            Spacer(modifier = Modifier.weight(0.1f))
            Icon(painter = painterResource(id = R.drawable.editicon)
                ,contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
data class SettingItem(val text: String, val action: () -> Unit)