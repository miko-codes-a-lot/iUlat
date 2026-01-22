package dev.cloudants.iulat.lib.ui.dashboard

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.cloudants.iulat.ui.theme.Purple200
import dev.cloudants.iulat.ui.theme.Purple500
import dev.cloudants.iulat.ui.theme.Teal200
import dev.cloudants.iulat.R
import dev.cloudants.iulat.lib.components.context.AdminReportItems
import dev.cloudants.iulat.lib.components.context.formatterDate
import dev.cloudants.iulat.lib.models.entities.DashboardReportItemDto
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.utils.main.MainNav
import dev.cloudants.iulat.lib.viewmodels.AdminReportViewModel
import dev.cloudants.iulat.ui.theme.Purple700
import dev.cloudants.iulat.ui.theme.PurpleGrey40


//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun DashboardPrev() {
//    Dashboard()
//}

@Composable
fun AdminDashboard(
     navController: NavController,
     currentUser: UserDto,
) {
    val context = LocalContext.current
    val viewModel: AdminReportViewModel = hiltViewModel()
    val percentages by viewModel.reportPercentages.collectAsState()
    val pieChartData by viewModel.pieChartData.collectAsState()
    val isDataEmpty = pieChartData.isEmpty() || pieChartData.values.sum() == 0
    val recentReports by viewModel.recentReports.collectAsState()
    val finalData = if (isDataEmpty) mapOf("No Reports" to 1) else pieChartData
    val finalColors = if (isDataEmpty) {
        listOf(Color.LightGray)
    } else {
        listOf(Purple200, Purple500, Teal200, Blue, Purple700, PurpleGrey40)
    }
    var showDialog by remember { mutableStateOf(false) }
    var announcementTitle by remember { mutableStateOf("") }
    var announcementMessage by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.background(Color.White),
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(MainNav.MapReports)
                    },
                    containerColor = Color.White,
                    contentColor = Color.Gray,
                    shape = CircleShape,
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = CircleShape
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.map_global),
                        contentDescription = "View Map Reports",
                            modifier = Modifier.size(34.dp)
                    )
                }

                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = Color.White,
                    contentColor = Color.Gray,
                    shape = CircleShape,
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = CircleShape
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.announcement),
                        contentDescription = "Add Announcement",
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item { PieChart(data = finalData, colors = finalColors) }
                item { Spacer(modifier = Modifier.height(8.dp)) }
                item { ReportList(reports = recentReports) }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    DashboardMenu(navController = navController, percentages = percentages)
                    }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                shape = RoundedCornerShape(28.dp),
                containerColor = Color.White,
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0xFF0049AD).copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.broadcast),
                                contentDescription = "Add Announcement",
                                modifier = Modifier.size(45.dp),
                                colorFilter = ColorFilter.tint(Color(0xFF0049AD))
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Broadcast Announcement",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0049AD),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "This will be sent to all registered users.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        OutlinedTextField(
                            value = announcementTitle,
                            onValueChange = { announcementTitle = it },
                            label = { Text("Announcement Title", color = Color(0xFF0049AD)) },
                            placeholder = { Text("e.g. Barangay Assembly") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0049AD),
                                unfocusedBorderColor = Color(0xFF0049AD),
                                cursorColor = Color.Gray
                            ),
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = announcementMessage,
                            onValueChange = { announcementMessage = it },
                            label = { Text("Detailed Message",color = Color(0xFF0049AD))},
                            placeholder = { Text("Enter the full details here...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0049AD),
                                unfocusedBorderColor = Color(0xFF0049AD),
                                cursorColor = Color.Gray
                            ),
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (announcementTitle.isNotBlank() && announcementMessage.isNotBlank()) {
                                viewModel.sendAnnouncement(
                                    announcementTitle,
                                    announcementMessage,
                                    currentUser.id ?: ""
                                )

                                Toast.makeText(
                                    context,
                                    "Announcement successfully to all resident!",
                                    Toast.LENGTH_LONG
                                ).show()

                                showDialog = false
                                announcementTitle = ""
                                announcementMessage = ""
                            } else {
                                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0049AD))
                    ) {
                        Text("Broadcast to Everyone", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun ReportList(
    reports: List<DashboardReportItemDto>
) {

    Column(modifier = Modifier.padding(top = 8.dp, end = 8.dp )) {
        TableHeader()
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (reports.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 70.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recent reports available",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                reports.forEach { item ->
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = Color.Gray,
                                spotColor = Color.Black
                            )
                            .padding(vertical = 4.dp)
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
                    ) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                        ) {
                            TableRow(item = item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableHeader() {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0049AD))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "ID", color = Color.White, fontSize = 14.sp)
            Text(text = "Title", color = Color.White, fontSize = 14.sp)
            Text(text = "Date", color = Color.White, fontSize = 14.sp)
            Text(text = "Status", color = Color.White, fontSize = 14.sp)
            Text(text = "Location", color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
fun TableRow(item: DashboardReportItemDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.reportId.takeLast(4),fontSize = 11.sp,modifier = Modifier.weight(2f))
//        Text(text = item.reportId, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(text = item.reportType, fontSize = 11.sp, modifier = Modifier.weight(2f))
        Text(text = formatterDate(item.reportDate), fontSize = 11.sp, modifier = Modifier.weight(2f))
        Text(text = item.status, fontSize = 11.sp, modifier = Modifier.weight(2f))
        Text(text = item.addressId, fontSize = 11.sp, modifier = Modifier.weight(2f), maxLines = 1)
    }
}


@Composable
fun PieChart(
    data: Map<String, Int>,
    colors: List<Color>,
    radiusOuter: Dp = 80.dp,
    chartBarWidth: Dp = 16.dp,
    animDuration: Int = 500,
) {

    val totalSum = data.values.sum()
    val floatValue = mutableListOf<Float>()

    val finalSum = if (totalSum == 0) 1 else totalSum
    data.values.forEachIndexed { index, values ->
        floatValue.add(index, 360 * values.toFloat() / finalSum.toFloat())
    }

    var animationPlayed by remember { mutableStateOf(false) }
    var lastValue = 0f

    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
        ), label = ""
    )

    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 11f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
        ), label = ""
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(
        modifier = Modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Gray,
                spotColor = Color.Black
            )
            .background(Color.White, shape = RoundedCornerShape(16.dp))
    ) {
        ElevatedCard(
            modifier = Modifier
                .background(Color.White)

        ) {
            Row(
                modifier = Modifier
                    .height(250.dp)
                    .background(Color.White)
                    .padding(10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 30.dp, start = 10.dp)
                        .size(animateSize.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(radiusOuter * 2f)
                            .rotate(animateRotation)
                    ) {
                        floatValue.forEachIndexed { index, value ->
                            drawArc(
                                color = colors[index % colors.size],
                                startAngle = lastValue,
                                sweepAngle = value,
                                useCenter = false,
                                style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                            )
                            lastValue += value
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(5.dp))
                Column(
                    modifier = Modifier
                        .padding(5.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    DetailsPieChart(
                        data = data,
                        colors = colors,
                        totalSum = totalSum
                    )
                }
            }
        }
    }
}

@Composable
fun DetailsPieChart(
    data: Map<String, Int>,
    colors: List<Color>,
    totalSum: Int
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 200.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(data.entries.toList().size) { index ->
            DetailsPieChartItem(
                data = data.entries.elementAt(index).toPair(),
                color = colors[index % colors.size],
                totalSum = totalSum
            )
        }
    }
}

@Composable
fun DetailsPieChartItem(
    data: Pair<String, Int>,
    totalSum: Int,
    height: Dp = 20.dp,
    color: Color
) {
    val percentage = if(totalSum > 0) (data.second.toFloat() / totalSum * 100).toInt() else 0
    val displayCount = if (totalSum == 0) 0 else data.second

    Surface(
        modifier = Modifier
            .padding(vertical = 5.dp),
        color = Color.Transparent
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .background(
                        color = color,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .size(height)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = data.first,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Color.Black
                )
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = "$displayCount ($percentage%)",
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun DashboardMenu(
    navController: NavController,
    percentages: Map<String, Float>
) {
    val baseItems = listOf(
        AdminReportItems(R.drawable.trash_can, "Garbage Disposal", 0f),
        AdminReportItems(R.drawable.ic_public_disturbance, "Public Disturbance", 0f),
        AdminReportItems(R.drawable.ic_robberies, "Robberies", 0f),
        AdminReportItems(R.drawable.streetlight, "Broken Streetlights", 0f),
        AdminReportItems(R.drawable.ic_vehicle_crashes, "Vehicle Crashes", 0f),
        AdminReportItems(R.drawable.road_work, "Road Repair", 0f),
        AdminReportItems(R.drawable.no_water, "No Water Supply", 0f),
        AdminReportItems(R.drawable.ic_others, "Others", 0f)
    )
    val reportItems = remember(percentages) {
        baseItems.map { item ->
            val realPercentage = percentages[item.title] ?: 0f
            item.copy(percentage = realPercentage)
        }
    }
    LazyColumn(
        modifier = Modifier
            .height(300.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(reportItems) { item ->
            DashboardButton(
                text = item.title,
                iconResId = item.icon,
                percentage = item.percentage,
                onClick = {
                    when (item.title) {
                        "Garbage Disposal" -> navController.navigate(MainNav.GarbageDisposalList)
                        "Public Disturbance" -> navController.navigate(MainNav.PublicDisturbanceList)
                        "Robberies" -> navController.navigate(MainNav.RobberiesList)
                        "Broken Streetlights" -> navController.navigate(MainNav.BrokenLightList)
                        "Vehicle Crashes" -> navController.navigate(MainNav.VehicleCrashesList)
                        "Road Repair" -> navController.navigate(MainNav.RoadRepairList)
                        "No Water Supply" -> navController.navigate(MainNav.NoWaterSupplyList)
                        "Others" -> navController.navigate(MainNav.OthersList)
                    }
                }
            )
        }
    }
}

@Composable
private fun DashboardButton(
    text: String,
    iconResId: Int? = null,
    percentage: Float,
    onClick: () -> Unit
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
            .clickable { onClick() },
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(8.dp)),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color.White,
                contentColor = Color(0xFF0049AD)
            ),
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .height(35.dp)
                    .background(Color.White)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                iconResId?.let {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = "Login Image",
                        modifier = Modifier.width(50.dp).height(190.dp)
                    )
                }
                Text(
                    text = text,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier
                        .weight(2f)
                )
                Text(
                    text = "${percentage.toInt()}%",
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier
                        .padding(end = 18.dp)
                )
            }
        }
    }
}


data class ReportItem(
    val id: String,
    val title: String,
    val date: String,
    val status: String,
    val location: String
)