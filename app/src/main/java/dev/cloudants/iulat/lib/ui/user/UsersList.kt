package dev.cloudants.iulat.lib.ui.user

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
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
                .padding(horizontal = 16.dp)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UsersSearchIcon(
                searchQuery = searchQuery,
                onSearchQueryChanged = { searchQuery = it },
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 50.dp)
                    .background(Color.White),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredUsers) { user ->
                    UsersSingleLine(
                        user = user,
                        onClick = {
                        }
                    )
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
fun UsersSingleLine(user: SimpleUser, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(51.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0049AD))
                    .border(1.dp, Color(0xFF0049AD), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                PlaceholderImage()
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    fontSize = 17.sp,
                    color = Color.Black,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = user.email,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}

@Composable
fun FloatParentFloatingIcon(navController: NavController) {
    FloatingActionButton(
        onClick = { navController.navigate(MainNav.CreateUser) },
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

data class SimpleUser(
    val firstName: String,
    val lastName: String,
    val email: String
)
