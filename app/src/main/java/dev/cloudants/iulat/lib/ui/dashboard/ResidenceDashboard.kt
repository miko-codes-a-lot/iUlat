package dev.cloudants.iulat.lib.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.cloudants.iulat.R
import dev.cloudants.iulat.lib.utils.main.MainNav

data class ResidenceReportItem(
    val iconRes: Int,
    val route: String,
    val title: String
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResidenceDashboardPrev() {
    ResidenceDashboard(navController = rememberNavController())
}

@Composable
fun ResidenceDashboard(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        CropsCategory(navController)
        Spacer(Modifier.weight(1f))
        Text(
            text = "Emergency Contacts",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start,
            color = Color.Black
        )

        EmergencyContactSection(navController)
    }
}

@Composable
fun CropsCategory(navController: NavController) {
    val reportItems = listOf(
        ResidenceReportItem(R.drawable.ic_vehicle_crashes, "healthReport", "Garbage Disposal"),
        ResidenceReportItem(R.drawable.ic_robberies, "crimeReport", "Public Disturbance"),
        ResidenceReportItem(R.drawable.ic_public_disturbance, "disturbanceReport", "Robberies"),
        ResidenceReportItem(R.drawable.ic_vehicle_crashes, "fireReport", "Broken Streetlights"),
        ResidenceReportItem(R.drawable.ic_vehicle_crashes, "educationReport", "Vehicle Crashes"),
        ResidenceReportItem(R.drawable.ic_vehicle_crashes, "environmentReport", "Road Repair"),
        ResidenceReportItem(R.drawable.ic_vehicle_crashes, "educationReport", "No Water Supply"),
        ResidenceReportItem(R.drawable.ic_vehicle_crashes, "educationReport", "Others"),
    )

    Column {
        for (i in reportItems.chunked(4)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                i.forEach { item ->
                    ReportCard(
                        iconRes = item.iconRes,
                        title = item.title,
                        onClick = {
                            navController.navigate(MainNav.CreateReport(title = item.title));
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ReportCard(
    iconRes: Int,
    title: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(80.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFF2568ef)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "Report Icon",
                modifier = Modifier.size(30.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EmergencyContactSection(navController: NavController) {
    val emergencyItems = listOf(
        ResidenceReportItem(R.drawable.ic_police, "police", "Rizal, Municipal Police Station (Rizal MPS)"),
        ResidenceReportItem(R.drawable.mdrrmo, "mddrmo", "Municipal Risk Reduction and Management Office Rizal"),
        ResidenceReportItem(R.drawable.ic_bfp, "bfp", "Bureau Fire Protection Rizal"),
        ResidenceReportItem(R.drawable.ic_coast_guard, "coast_guard", "Philippine Coast Guard")
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(emergencyItems) { item ->
            EmergencyContactCard(
                iconRes = item.iconRes,
                title = item.title,
                onClick = { navController.navigate(item.route) }
            )
        }
    }
}

@Composable
fun EmergencyContactCard(
    iconRes: Int,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clickable { onClick() },
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2568ef)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "Emergency Icon",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}