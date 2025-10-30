package dev.cloudants.iulat.lib.ui.message

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import dev.cloudants.iulat.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import dev.cloudants.iulat.lib.ui.report.UsersImageContainer
import dev.cloudants.iulat.lib.utils.main.MainNav

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MessageListPrev() {
  MessageList(navController = rememberNavController())
}

@Composable
fun MessageList(navController: NavController) {
    val users = listOf(
        User(
            id = "1",
            firstName = "Jericho",
            lastName = "Me",
            imageUri = null,

        ),
        User(
            id = "2",
            firstName = "Maria",
            lastName = "Santos",
            imageUri = null,
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp)
        ) {
            items(users) { user ->
                SingleItemMessageCard(
                    userName = "${user.firstName} ${user.lastName}",
                    imageUrl = user.imageUri?.toString() ?: "",
                    onClick = {
                        navController.navigate(MainNav.Message)
                    },
                    onDeleteClick = {

                    },
                    onCheckClick = {

                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun UsersImageContainer(imageUrl: String) {
    Box(
        modifier = Modifier
            .size(51.dp)
            .clip(CircleShape)
            .background(Color(0xFF0049AD)),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNotEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(47.dp)
                    .clip(CircleShape)
            )
        } else {
            PlaceholderImage()
        }
    }
}

@Composable
fun SingleItemMessageCard(
    userName: String,
    imageUrl: String,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCheckClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Gray,
                spotColor = Color.Black
            )
            .padding(vertical = 4.dp)
    ) {
        ElevatedCard(
            modifier = Modifier
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(8.dp)),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color.White,
                contentColor = Color(0xFF0049AD)
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, bottom = 5.dp)
                    .height(65.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(10.dp))
                    UsersImageContainer(imageUrl = imageUrl)
                    Column {
                        Text(
                            text = userName,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                }
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color(0xFF0049AD)
                )
            }
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

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val imageUri: Uri?,
)

