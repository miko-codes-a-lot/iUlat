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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
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
                val report = reports.find { it.docId == item.reportId }
                if (report != null) {
                    navController.navigate(
                        MainNav.NotificationReportVIew(
                            title = report.reportType,
                            reportId = report.docId
                        )
                    )
                } else {
                    Log.e("NotifNav", "Report not found in current list for ID: ${item.reportId}")
                }
            }
        )
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
                text = if (notify.message.length > 25) notify.message.take(25) + "..." else notify.message,
                fontSize = 15.sp,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
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