package dev.cloudants.iulat.lib.ui.report.residence_report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.cloudants.iulat.lib.components.context.MODULE
import dev.cloudants.iulat.lib.components.context.formatterDate
import dev.cloudants.iulat.lib.components.header.CustomHeader
import dev.cloudants.iulat.lib.models.entities.PublicDisturbanceDto
import dev.cloudants.iulat.lib.models.entities.RoadRepairDto
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.utils.main.MainNav
import dev.cloudants.iulat.lib.viewmodels.PublicDisturbanceViewModel
import dev.cloudants.iulat.lib.viewmodels.RoadRepairViewModel
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.res.painterResource
import dev.cloudants.iulat.R
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import dev.cloudants.iulat.MainActivity
import dev.cloudants.iulat.lib.components.context.PrintableRowImpl
import dev.cloudants.iulat.lib.components.print.Print
import dev.cloudants.iulat.lib.components.print.exportDynamicPDF

@Composable
fun RoadRepairList(
    navController: NavController,
    currentUser: UserDto
) {
    val roadRepairViewModel: RoadRepairViewModel = hiltViewModel()
    val state by roadRepairViewModel.state.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (currentUser.isResidence) {
            roadRepairViewModel.fetchAll(currentUser.id!!)
        } else {
            roadRepairViewModel.fetchAllRoadRepair()
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)),
        floatingActionButton = {
            FloatingRoadRepairRecordIcon(
                navController,
                currentUser,
                context,
                items = state.items
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomHeader(MODULE.ROAD_REPAIR)
            if (state.isLoading) {
                CircularProgressIndicator(color = Color(0xFF0049AD))
            }
            RoadRepairListContainer(
                navController = navController,
                items = state.items
            )
        }
    }
}

@Composable
fun RoadRepairListContainer(
    navController: NavController,
    items: List<RoadRepairDto>
) {
    LazyColumn(
        modifier = Modifier
            .padding(bottom = 50.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (items.isEmpty()) {
            item {
                Text(
                    text = "No road repair reports found.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        items(items) { report ->
            RoadRepairButton(navController, report)
        }
    }
}

@Composable
private fun RoadRepairButton(
    navController: NavController,
    report: RoadRepairDto,
) {
    ElevatedButton(
        onClick = {
            report.id?.let { id ->
                if (report.status == "Rejected") {
                    navController.navigate(MainNav.EditReport("Road Repair", id))
                } else {
                    navController.navigate(MainNav.ViewReport("Road Repair", id))
                }
            }
        },
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
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = formatterDate(report.createdAt),
                fontSize = 15.sp,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${report.status}",
                fontSize = 15.sp,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun FloatingRoadRepairRecordIcon(
    navController: NavController,
    currentUser: UserDto,
    context: Context,
    items: List<RoadRepairDto>
) {
    val activity = context as? MainActivity
    Column(
        modifier = Modifier.background(Color.Transparent),
        horizontalAlignment = Alignment.End
    ) {
        if (currentUser.isAdmin) {
            FloatingActionButton(
                onClick = {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && activity != null) {
                        activity.requestStoragePermission()
                    }
                    val pdfRows = items.map { report ->
                        PrintableRowImpl(
                            listOf(
                                formatterDate(report.createdAt),
                                report.status
                            )
                        )
                    }
                    exportDynamicPDF(
                        context = context,
                        title = "Road Repair",
                        headers = listOf("No", "Date Created", "Status"),
                        data = pdfRows,
                        onFinish = { Print.openFile(context, it) },
                        onError = { Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show() }
                    )
                },
                containerColor = Color(0xFF0049AD),
                contentColor = Color(0xFFFFFFFF),
                shape = CircleShape,
                modifier = Modifier
                    .size(75.dp)
                    .offset(x = (-5).dp, y = (-7).dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.printer),
                    contentDescription = "Export",
                    modifier = Modifier.size(30.dp),
                    tint = Color.White
                )
            }
        } else {
            FloatingActionButton(
                onClick = {
                    navController.navigate(MainNav.CreateReport("Road Repair"))
                },
                containerColor = Color(0xFF0049AD),
                contentColor = Color(0xFFFFFFFF),
                shape = CircleShape,
                modifier = Modifier
                    .size(75.dp)
                    .offset(x = (-5).dp, y = (-7).dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}
