package dev.cloudants.iulat.lib.ui.notification

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import dev.cloudants.iulat.lib.models.entities.NotificationItem
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.cloudants.iulat.lib.components.context.formatterDate
import dev.cloudants.iulat.lib.utils.main.MainNav
import dev.cloudants.iulat.lib.viewmodels.AdminReportViewModel
import dev.cloudants.iulat.lib.viewmodels.NotificationViewModel

@Composable
fun NotificationList(navController: NavController) {
    val viewModel: NotificationViewModel = hiltViewModel()
    val notifications by viewModel.notificationUiState.collectAsStateWithLifecycle()
    val adminReportViewModel: AdminReportViewModel = hiltViewModel()
    val reports by adminReportViewModel.reports.collectAsState()
    var selectedAnnouncement by remember { mutableStateOf<NotificationItem?>(null) }
    LaunchedEffect(Unit) {
        adminReportViewModel.loadReports()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(top = 35.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 0.dp, vertical = 4.dp)
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = Color(0xFF0049AD),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notifications",
                fontSize = 25.sp,
                color = Color(0xFF0049AD),
                fontWeight = FontWeight.W800,
                fontFamily = FontFamily.SansSerif
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Notifications(
            notifyList = notifications,
            onNotifClick = { item ->
                viewModel.onNotificationClicked(item.id)
                if (item.reportId.isNullOrEmpty()) {
                    selectedAnnouncement = item
                } else {
                    val report = reports.find { it.docId == item.reportId }
                    if (report != null) {
                        navController.navigate(
                            MainNav.NotificationReportVIew(
                                title = report.reportType,
                                reportId = report.docId
                            )
                        )
                    } else {
                        Log.e("NotifNav", "Reference report not found for ID: ${item.reportId}")
                    }
                }
            }
        )
        if (selectedAnnouncement != null) {
            AlertDialog(
                onDismissRequest = { selectedAnnouncement = null },
                shape = RoundedCornerShape(24.dp),
                containerColor = Color.White,
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color(0xFF0049AD).copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color(0xFF0049AD),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = selectedAnnouncement?.reportType ?: "Barangay Announcement",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0049AD),
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )

                        Text(
                            text = selectedAnnouncement?.message ?: "",
                            fontSize = 16.sp,
                            color = Color(0xFF42474E),
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Posted on ${formatterDate(selectedAnnouncement?.createdAt.toString())}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontStyle = FontStyle.Italic
                        )
                    }
                },
                confirmButton = {
                    ElevatedButton(
                        onClick = { selectedAnnouncement = null },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFF0049AD),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Understood", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun Notifications(
    notifyList: List<NotificationItem>,
    onNotifClick: (NotificationItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(notifyList) { item ->
            NotificationButton(
                notify = item,
                onClick = { onNotifClick(item) }
            )
        }
    }
}

@Composable
private fun NotificationButton(
    notify: NotificationItem,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF0049AD)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .padding(3.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (!notify.isRead) {
                Box(
                    modifier = Modifier
                        .background(color = Color.Red, shape = CircleShape)
                        .size(13.dp)
                        .border(width = 1.dp, color = Color.Red, shape = CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = if (!notify.reportId.isNullOrEmpty()) {
                    if (notify.message.length > 25) notify.message.take(25) + "..." else notify.message
                } else {
                    notify.reportType ?: "New Announcement"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )

            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formatterDate(notify.createdAt.toString()),
                fontSize = 11.sp,
                textAlign = TextAlign.End,
                color = Color.Gray,
                fontFamily = FontFamily.SansSerif,
                maxLines = 1
            )
        }
    }
}