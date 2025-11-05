package dev.cloudants.iulat.lib.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.cloudants.iulat.ui.theme.Purple200
import dev.cloudants.iulat.ui.theme.Purple500
import dev.cloudants.iulat.ui.theme.Teal200
import dev.cloudants.iulat.R


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPrev() {
    Dashboard()
}

@Composable
fun Dashboard() {

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PieChart(
            data = mapOf(
                Pair("Garbage Disposal", 150),
                Pair("Public Disturbance", 120),
                Pair("Robberies", 110),
                Pair("Broken Streetlights", 170),
//                Pair("Vehicle Crashes", 120),
//                Pair("Road Repair", 120),
//                Pair("No Water Supply", 120),
//                Pair("Others", 120),
            )
        )

        Spacer(modifier = Modifier.height(8.dp))
        ReportList()
        Spacer(modifier = Modifier.height(8.dp))
        DashboardMenu()
    }
}

@Composable
fun ReportList() {
    val reportItems = listOf(
        ReportItem("1", "Title 1", "2025-10-01", "Active", "Location 1"),
        ReportItem("2", "Title 2", "2025-10-02", "Inactive", "Location 2"),
        ReportItem("3", "Title 3", "2025-10-03", "Pending", "Location 3"),
        ReportItem("4", "Title 4", "2025-10-04", "Active", "Location 4"),
        ReportItem("5", "Title 5", "2025-10-05", "Inactive", "Location 5")
    )

    Column(modifier = Modifier.padding(top = 8.dp, end = 8.dp )) {
        TableHeader()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            items(reportItems) { item ->
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
fun TableRow(item: ReportItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.id, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(text = item.title, fontSize = 12.sp, modifier = Modifier.weight(2f))
        Text(text = item.date, fontSize = 12.sp, modifier = Modifier.weight(2f))
        Text(text = item.status, fontSize = 12.sp, modifier = Modifier.weight(2f))
        Text(text = item.location, fontSize = 12.sp, modifier = Modifier.weight(2f))
    }
}


@Composable
fun PieChart(
    data: Map<String, Int>,
    radiusOuter: Dp = 80.dp,
    chartBarWidth: Dp = 16.dp,
    animDuration: Int = 500,
) {

    val totalSum = data.values.sum()
    val floatValue = mutableListOf<Float>()

    data.values.forEachIndexed { index, values ->
        floatValue.add(index, 360 * values.toFloat() / totalSum.toFloat())
    }

    val colors = listOf(
        Purple200,
        Purple500,
        Teal200,
        Blue,
    )

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
                                color = colors[index],
                                lastValue,
                                value,
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
                        colors = colors
                    )
                }
            }
        }
    }
}

@Composable
fun DetailsPieChart(
    data: Map<String, Int>,
    colors: List<Color>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        data.values.forEachIndexed { index, value ->
            DetailsPieChartItem(
                data = Pair(data.keys.elementAt(index), value),
                color = colors[index]
            )
        }

    }
}

@Composable
fun DetailsPieChartItem(
    data: Pair<String, Int>,
    height: Dp = 20.dp,
    color: Color
) {

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
                    text = data.second.toString(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun DashboardMenu() {
    val reportItems = listOf(
        Triple(R.drawable.trash_can, "Garbage Disposal", 15f),
        Triple(R.drawable.ic_public_disturbance, "Public Disturbance", 20f),
        Triple(R.drawable.ic_robberies, "Robberies", 10f),
        Triple(R.drawable.streetlight, "Broken Streetlights", 25f),
        Triple(R.drawable.ic_vehicle_crashes, "Vehicle Crashes", 5f),
        Triple(R.drawable.road_work, "Road Repair", 18f),
        Triple(R.drawable.no_water, "No Water Supply", 12f),
        Triple(R.drawable.ic_others, "Others", 30f)
    )

    LazyColumn(
        modifier = Modifier
            .height(168.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(reportItems) { (imageResId, text, percentage) ->
            DashboardButton(
                text = text,
                iconResId = imageResId,
                percentage = percentage
            )
        }
    }
}

@Composable
private fun DashboardButton(
    text: String,
    iconResId: Int? = null,
    percentage: Float
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