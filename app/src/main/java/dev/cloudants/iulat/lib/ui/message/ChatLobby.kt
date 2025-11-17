package dev.cloudants.iulat.lib.ui.message

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import dev.cloudants.iulat.lib.utils.main.MainNav
import dev.cloudants.iulat.lib.viewmodels.ChatViewModel
import kotlinx.coroutines.delay

data class ChatUser(
    val id: String,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val userProfile: String?,
    val lastMessage: String,
    val isRead: Boolean
)

@Composable
fun ChatLobby(
    navController: NavController,
    currentUserId: String
) {
    val viewModel: ChatViewModel = hiltViewModel()
    val users by viewModel.users.collectAsState()

    LaunchedEffect(currentUserId) {
        viewModel.loadUsers(currentUserId)
    }

    var searchQuery by remember { mutableStateOf("") }
    val filteredUsers = remember(users, searchQuery) {
        if (searchQuery.isEmpty()) users
        else users.filter {
            val fullName = "${it.userDto.firstName} ${it.userDto.middleName.orEmpty()} ${it.userDto.lastName}"
            fullName.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        SearchMessageIcon(
            searchQuery = searchQuery,
            onSearchQueryChanged = { searchQuery = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredUsers) { userChat ->
                ChatCard(
                    navController = navController,
                    chatUser = ChatUser(
                        id = userChat.userDto.id!!,
                        firstName = userChat.userDto.firstName,
                        middleName = userChat.userDto.middleName,
                        lastName = userChat.userDto.lastName,
                        userProfile = userChat.userDto.userProfile,
                        lastMessage = userChat.chatDto.lastMessage,
                        isRead = userChat.chatDto.isRead
                    )
                )
            }
        }
    }
}

@Composable
fun ChatCard(navController: NavController, chatUser: ChatUser) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(MainNav.ChatDirect(userId = chatUser.id))
            },
        colors = CardDefaults.cardColors(
            contentColor = Color.Black,
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CardImageContainer(chatUser.userProfile)
            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "${chatUser.firstName} ${chatUser.middleName.orEmpty()} ${chatUser.lastName}",
                    fontSize = 15.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = chatUser.lastMessage,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = if (chatUser.isRead) FontWeight.Normal else FontWeight.Bold,
                    color = if (chatUser.isRead) Color.Gray else Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            if (!chatUser.isRead) {
                Box(
                    modifier = Modifier
                        .background(Color.Red, shape = CircleShape)
                        .size(13.dp)
                        .border(1.dp, Color.Red, CircleShape)
                )
            }
        }
    }
}

@Composable
fun CardImageContainer(imageBase64: String? = null) {
    Box(
        modifier = Modifier
            .size(51.dp)
            .clip(CircleShape)
            .border(1.dp, Color(0xFF0049AD), CircleShape)
            .background(Color(0xFF0049AD)),
        contentAlignment = Alignment.Center
    ) {
        if (imageBase64 != null && imageBase64.isNotBlank()) {
            val resizedBitmap = remember(imageBase64) {
                decodeBase64ToBitmap(imageBase64)?.let { bitmap ->
                    resizeBitmap(bitmap, 500, 500)
                }
            }

            if (resizedBitmap != null) {
                Image(
                    bitmap = resizedBitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(51.dp)
                        .clip(CircleShape)
                )
            } else {
                PlaceholderImage()
            }
        } else {
            PlaceholderImage()
        }
    }
}

@Composable
fun PlaceholderImage() {
    Image(
        painter = rememberAsyncImagePainter(model = "https://img.freepik.com/premium-vector/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-vector-illustration_561158-3467.jpg"),
        contentDescription = "User's avatar",
        modifier = Modifier
            .size(51.dp)
            .clip(CircleShape)
            .background(Color(0xFF0049AD)),
    )
}

@Composable
fun SearchMessageIcon(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = searchQuery,
        onValueChange = { onSearchQueryChanged(it) },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", tint = Color.Black)
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChanged("") }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear", tint = Color.Black)
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            cursorColor = Color.Black
        ),
        placeholder = {
            Text(
                text = "Search message...",
                color = Color.Gray,
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp
            )
        }
    )
}

fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }
}

fun resizeBitmap(source: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val ratio = minOf(maxWidth.toFloat() / source.width, maxHeight.toFloat() / source.height)
    val width = (ratio * source.width).toInt()
    val height = (ratio * source.height).toInt()
    return Bitmap.createScaledBitmap(source, width, height, true)
}
