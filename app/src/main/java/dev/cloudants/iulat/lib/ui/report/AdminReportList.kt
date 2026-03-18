package dev.cloudants.iulat.lib.ui.report

import android.util.Log
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import dev.cloudants.iulat.R
import dev.cloudants.iulat.lib.components.context.formatterDate
import dev.cloudants.iulat.lib.components.context.formatterToFilterMonth
import dev.cloudants.iulat.lib.components.context.formatterToFilterWeek
import dev.cloudants.iulat.lib.components.context.getCurrentWeekString
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.utils.main.MainNav
import dev.cloudants.iulat.lib.viewmodels.AdminReportViewModel

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportPreview() {
    AdminReportList(navController = rememberNavController())
}

@Composable
fun AdminReportList(navController: NavController) {
    val adminReportViewModel: AdminReportViewModel = hiltViewModel()
    val reports by adminReportViewModel.reports.collectAsState()

    var selectedStatus by remember { mutableStateOf("Pending") }
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableStateOf<String?>(null) }
    val currentWeekString = remember { getCurrentWeekString() }
    val isWeeklyView = selectedStatus == "Rejected" || selectedStatus == "Resolved"
    LaunchedEffect(searchQuery) {
        delay(500L)
        debouncedQuery = searchQuery
    }
    LaunchedEffect(selectedStatus, debouncedQuery) {
        adminReportViewModel.loadReportsByStatus(selectedStatus, debouncedQuery)
        selectedMonth = null
    }

    val filteredReports by remember(reports, selectedMonth, isWeeklyView, currentWeekString) {
        derivedStateOf {
            if (isWeeklyView) {
                reports.filter { reportItem ->
                    formatterToFilterWeek(reportItem.reportDate) == currentWeekString
                }
            } else if (selectedMonth == null) {
                reports
            } else {
                reports.filter { reportItem ->
                    formatterToFilterMonth(reportItem.reportDate) == selectedMonth
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SearchReportIcon(
            searchQuery = searchQuery,
            onSearchQueryChanged = { searchQuery = it },
        )
        if (!isWeeklyView) {
            MonthFilterCard(
                selectedMonth = selectedMonth,
                onMonthSelected = { selectedMonth = it }
            )
        }
        ReportTableHeader(
            selectedStatus = selectedStatus,
            onStatusSelected = { selectedStatus = it }
        )

        LazyColumn(
            modifier = Modifier
                .padding(top = 5.dp, bottom = 5.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (filteredReports.isEmpty()) {
                item {
                    Text(
                        text = "No Report found",
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            }
            items(filteredReports) { reportItem ->
                SingleItemCard(
                    title = reportItem.reportType,
                    status = reportItem.status,
                    userName = "from : " + reportItem.userName,
                    date = formatterDate(reportItem.reportDate),
                    onClick = {
                        val status = reportItem.status.trim()
                        val type = reportItem.reportType.trim()
                        val docId = reportItem.docId
                        when (status) {
                            "Pending", "Rejected" -> {
                                navController.navigate(MainNav.ViewPendingReport(type, docId))
                            }

                            "Approve", "Approved" -> {
                                navController.navigate(MainNav.ViewReport(type, docId))
                            }

                            "Resolved" -> {
                                navController.navigate(MainNav.Map(addressId = docId, reportType = type, status = status))
                            }

                            else -> {
                                Log.d("Navigation", "Unhandled status: $status. Defaulting to Map.")
                                navController.navigate(MainNav.Map(addressId = docId, reportType = type, status = status))
                            }
                        }
                    },
                    onDeleteClick = {
                        adminReportViewModel.updateReportStatus(reportItem, "Rejected")
                        selectedStatus = "Rejected"
                        adminReportViewModel.loadReportsByStatus("Rejected", debouncedQuery)
                    },
                    onCheckClick = {
                        val nextStatus = when(reportItem.status) {
                            "Approve" -> "Resolved"
                            else -> "Approve"
                        }
                        adminReportViewModel.updateReportStatus(reportItem, nextStatus)
                        selectedStatus = nextStatus
                        adminReportViewModel.loadReportsByStatus(nextStatus, debouncedQuery)
                    }
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
            IconButton(onClick = { }) {
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
fun ReportTableHeader(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {
    val statuses = listOf("Pending", "Rejected", "Approve", "Resolved")
    ElevatedCard(
        modifier = Modifier
            .padding(start = 2.dp, end = 2.dp)
            .fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White,
            contentColor = Color(0xFF0049AD)
        )
    ) { Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                statuses.forEach { status ->
                    ReportStatusBox(
                        status = status,
                        isSelected = selectedStatus == status,
                        onClick = { onStatusSelected(status) }
                    )
                }
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
            .border(
                2.dp,
                if (isSelected) Color(0xFF0049AD) else Color.Gray,
                RoundedCornerShape(5.dp)
            ),
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
fun UsersImageContainer() {
    Box(
        Modifier
            .size(51.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color.Gray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.report ),
                contentDescription = "Notifications",
                tint = Color.Gray,
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
fun SingleItemCard(
    title: String,
    status: String,
    userName: String,
    date: String,
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
            .clickable { onClick() }
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
                    UsersImageContainer()
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
                    when (status) {
                        "Pending" -> {
                            IconButton(onClick = onDeleteClick) {
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
                                IconButton(onClick = onCheckClick) {
                                    Image(
                                        painter = painterResource(id = R.drawable.checklist),
                                        contentDescription = "Check Icon",
                                        modifier = Modifier.size(42.dp)
                                    )
                                }
                            }
                        }

                        "Approve" -> {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2568ef))
                            ) {
                                IconButton(onClick = onCheckClick) {
                                    Image(
                                        painter = painterResource(id = R.drawable.checklist),
                                        contentDescription = "Check Icon",
                                        modifier = Modifier.size(42.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))

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
fun PlaceholderImage(activeUser: UserDto? = null) {
    val imageSource = if (!activeUser?.imageBase64.isNullOrEmpty()) {
        val imageBytes = android.util.Base64.decode(activeUser.imageBase64, android.util.Base64.DEFAULT)
        imageBytes
    } else {
        activeUser?.userProfile ?: "https://img.freepik.com/premium-vector/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-vector-illustration_561158-3467.jpg"
    }
    Image(
        painter = rememberAsyncImagePainter(model = imageSource),
        contentDescription = "User's avatar",
        modifier = Modifier
            .size(51.dp)
            .clip(CircleShape)
            .background(Color(0xFF0049AD)),
    )
}
