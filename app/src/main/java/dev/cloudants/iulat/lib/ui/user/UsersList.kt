package dev.cloudants.iulat.lib.ui.user

import androidx.compose.foundation.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.cloudants.iulat.lib.ui.report.PlaceholderImage
import dev.cloudants.iulat.lib.utils.main.MainNav

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PreviewUsersUI() {
    UsersList(navController = rememberNavController())
}

@Composable
fun UsersList(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    val mockUsers = listOf(
        SimpleUser("John", "Doe", "john@example.com"),
        SimpleUser("Jane", "Smith", "jane@example.com"),
        SimpleUser("Carlos", "Reyes", "carlosr@example.com"),
        SimpleUser("Maria", "Dela Cruz", "mariadc@example.com")
    )

    val filteredUsers = mockUsers.filter {
        it.firstName.contains(searchQuery, ignoreCase = true) ||
                it.lastName.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        floatingActionButton = {
            FloatParentFloatingIcon(navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
                .padding(padding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UsersSearchIcon(
                searchQuery = searchQuery,
                onSearchQueryChanged = { searchQuery = it },
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(filteredUsers) { user ->
                    UsersSingleLine(user)
                }
            }
        }
    }
}

@Composable
fun UsersSearchIcon(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search Icon",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChanged("") }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Clear Search",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Black,
            focusedBorderColor = Color.Black,
            cursorColor = Color.Black
        ),
        placeholder = {
            Text(text = "Search...", color = Color.Gray)
        }
    )
}

@Composable
fun UsersSingleLine(user: SimpleUser) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
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
//                        UsersImage(imageUrl = imageUrl)
                        UsersImage()
                        Column {
                            Text(
                                text = "${user.firstName} ${user.lastName}",
                                fontSize = 18.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            )
                            Text(
                                text = user.email,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = Color.Gray,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            )
                        }
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
}

@Composable
fun FloatParentFloatingIcon(navController: NavController) {
    Column(
        modifier = Modifier
            .background(Color.Transparent),
        horizontalAlignment = Alignment.End
    ) {
        FloatingActionButton(
            onClick = {
                navController.navigate(MainNav.CreateUser)
            },
            containerColor = Color(0xFF0049AD),
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.size(65.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun UsersImage() {
    Box(
        Modifier
            .size(51.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(Color(0xFF0049AD))
                .border(1.dp, Color(0xFF0049AD), CircleShape),
            contentAlignment = Alignment.Center
        ) {
//            if (imageUrl.isNotBlank()) {
//                Image(
//                    painter = rememberAsyncImagePainter(model = imageUrl),
//                    contentDescription = "User's avatar",
//                    modifier = Modifier
//                        .size(51.dp)
//                        .clip(CircleShape),
//                    contentScale = ContentScale.Crop
//                )
//            } else {
                PlaceholderImage()
//            }
        }
    }
}

data class SimpleUser(
    val firstName: String,
    val lastName: String,
    val email: String
)
