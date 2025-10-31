package dev.cloudants.iulat.lib.ui.report

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import dev.cloudants.iulat.R


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportPreview() {
    Report()
}

@Composable
fun Report() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var debouncedQuery by remember { mutableStateOf("") }
        var searchQuery by remember { mutableStateOf("") }

        val reportItems = listOf(
            ReportListItem(
                title = "Public Disturbance",
                userName = "Jericho Me - Tagumpay II",
                date = "March 2, 2025",
                imageUrl = "https://img.freepik.com/premium-vector/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-vector-illustration_561158-3467.jpg"
            ),
            ReportListItem(
                title = "Noise Complaint",
                userName = "Juan Dela Cruz - Maligaya 5",
                date = "March 3, 2025",
                imageUrl = "https://img.freepik.com/premium-vector/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-vector-illustration_561158-3467.jpg"
            ),
            ReportListItem(
                title = "Noise Complaint",
                userName = "Juan Dela Cruz - Maligaya 5",
                date = "March 3, 2025",
                imageUrl = "https://img.freepik.com/premium-vector/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-vector-illustration_561158-3467.jpg"
            ),

            )
        LaunchedEffect(searchQuery) {
            delay(500L)
            debouncedQuery = searchQuery
        }
        SearchReportIcon(
            searchQuery = searchQuery,
            onSearchQueryChanged = { searchQuery = it },
        )
        Spacer(modifier = Modifier.height(10.dp))
        ReportTableHeader()

        LazyColumn(
            modifier = Modifier
                .padding(top = 5.dp, bottom = 5.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(reportItems) { reportItem ->
                SingleItemCard(
                    title = reportItem.title,
                    userName = reportItem.userName,
                    date = reportItem.date,
                    imageUrl = reportItem.imageUrl,
                    onClick = { /* Handle card click */ },
                    onDeleteClick = { /* Handle delete click */ },
                    onCheckClick = { /* Handle check click */ }
                )
            }
        }
    }
}


@Composable
fun SearchReportIcon(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = searchQuery,
        onValueChange = { onSearchQueryChanged(it) },
        leadingIcon = {
            IconButton(onClick = { } ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
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
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedBorderColor = Color.Black,
            disabledBorderColor = Color.Gray,
            errorBorderColor = Color.Red,
            cursorColor = Color.Black
        ),
        placeholder = {
            Text(
                text = "Search Report...",
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp
            )
        }
    )
}

@Composable
fun ReportTableHeader() {
    var selectedItem by remember { mutableStateOf("") }
    ElevatedCard(
        modifier = Modifier
            .padding(start = 2.dp, end = 2.dp)
            .fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White,
            contentColor = Color(0xFF0049AD)
        )
    ) {
        Column{
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ReportStatusBox(
                    status = "New",
                    isSelected = selectedItem == "New",
                    onClick = { selectedItem = "New" }
                )
                ReportStatusBox(
                    status = "Pending",
                    isSelected = selectedItem == "Pending",
                    onClick = { selectedItem = "Pending" }
                )
                ReportStatusBox(
                    status = "Resolved",
                    isSelected = selectedItem == "Resolved",
                    onClick = { selectedItem = "Resolved" }
                )
                ReportStatusBox(
                    status = "Rejected",
                    isSelected = selectedItem == "Rejected",
                    onClick = { selectedItem = "Rejected" }
                )
            }
        }
    }
}

@Composable
fun ReportStatusBox(
    status: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .height(40.dp)
            .background(
                color = if (isSelected) Color(0xFF0049AD) else Color.Transparent,
                shape = RoundedCornerShape(5.dp)
            )
            .border(2.dp, if (isSelected) Color(0xFF0049AD) else Color.Gray, RoundedCornerShape(5.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status,
            color = if (isSelected) Color.White else Color.Black,
            fontSize = 14.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun UsersImageContainer(imageUrl: String) {
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
            if (imageUrl.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = "User's avatar",
                    modifier = Modifier
                        .size(51.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                PlaceholderImage()
            }
        }
    }
}

@Composable
fun SingleItemCard(
    title: String,
    userName: String,
    date: String,
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
                            text = title,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier
                                .padding(start = 8.dp, bottom = 4.dp)
                                .weight(1f),
                            color = Color.Black
                        )
                        Text(
                            text = userName,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            color = Color.Black
                        )
                        Text(
                            text = date,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = onDeleteClick
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cross),
                            contentDescription = "Delete Icon",
                            modifier = Modifier.size(45.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2568ef))
                    ) {
                        IconButton(
                            onClick = onCheckClick
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.checklist),
                                contentDescription = "Check Icon",
                                modifier = Modifier.size(42.dp)
                            )
                        }
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

data class ReportListItem(
    val title: String,
    val userName: String,
    val date: String,
    val imageUrl: String
)
