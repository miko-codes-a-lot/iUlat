package dev.cloudants.iulat.lib.ui.report.residence_report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import dev.cloudants.iulat.lib.components.print.Print
import dev.cloudants.iulat.lib.components.print.exportDynamicPDF
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import dev.cloudants.iulat.lib.components.context.PrintableRowImpl
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
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.res.painterResource
import dev.cloudants.iulat.R
import android.os.Build
import android.os.Environment
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.cloudants.iulat.MainActivity
import dev.cloudants.iulat.lib.components.context.MODULE
import dev.cloudants.iulat.lib.components.context.formatterDate
import dev.cloudants.iulat.lib.components.context.formatterToFilterMonth
import dev.cloudants.iulat.lib.components.header.CustomHeader
import dev.cloudants.iulat.lib.models.entities.NoWaterSupplyDto
import dev.cloudants.iulat.lib.models.entities.PublicDisturbanceDto
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.ui.report.MonthFilterCard
import dev.cloudants.iulat.lib.utils.main.MainNav
import dev.cloudants.iulat.lib.viewmodels.NoWaterSupplyViewModel
import dev.cloudants.iulat.lib.viewmodels.PublicDisturbanceViewModel
import dev.cloudants.iulat.lib.viewmodels.UserViewModel


@Composable
fun PublicDisturbanceList(
    navController: NavController,
    currentUser: UserDto
) {
    val publicDisturbanceViewModel: PublicDisturbanceViewModel = hiltViewModel()
    val state by publicDisturbanceViewModel.state.collectAsState()
    val userViewModel: UserViewModel = hiltViewModel()
    val users by userViewModel.users.collectAsState()
    val context = LocalContext.current
    var selectedMonth by remember { mutableStateOf<String?>(null) }
    val filteredItems = remember(state.items, selectedMonth) {
        if (selectedMonth == null) {
            state.items
        } else {
            state.items.filter { formatterToFilterMonth(it.createdAt) == selectedMonth }
        }
    }
    LaunchedEffect(Unit) {
        userViewModel.loadUsers()
        if(currentUser.isResidence) {
            publicDisturbanceViewModel.fetchAll(currentUser.id!!)
        } else {
            publicDisturbanceViewModel.fetchAllPublicDisturbance()
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)),
        floatingActionButton = {
            val canSeeButton = currentUser.isAdmin || (currentUser.isResidence && currentUser.isVerified)
            if (canSeeButton) {
                FloatingPublicDisturbanceRecordIcon(
                    navController,
                    currentUser,
                    context,
                    items = filteredItems,
                    users = users
                )
            }
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
            CustomHeader(MODULE.PUBLIC_DISTURBANCE)
            MonthFilterCard(
                selectedMonth = selectedMonth,
                onMonthSelected = { selectedMonth = it }
            )
            if (state.isLoading) {
                CircularProgressIndicator(color = Color(0xFF0049AD))
            }
            PublicDisturbanceListContainer(
                navController = navController,
                items = filteredItems
            )
        }
    }
}

@Composable
fun PublicDisturbanceListContainer(
    navController: NavController,
    items: List<PublicDisturbanceDto>
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
                    text = "No public disturbance reports found.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        items(items) { report ->
            PublicDisturbanceButton(navController, report)
        }
    }
}

@Composable
private fun PublicDisturbanceButton(
    navController: NavController,
    report: PublicDisturbanceDto,
) {
    ElevatedButton(
        onClick = {
            report.id?.let { id ->
                navController.navigate(MainNav.ViewPendingReport("Public Disturbance", id))
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
fun FloatingPublicDisturbanceRecordIcon(
    navController: NavController,
    currentUser: UserDto,
    context: Context,
    items: List<PublicDisturbanceDto>,
    users: List<UserDto>
) {
    val activity = context as? MainActivity
    val userMap = remember(users) {
        users.associateBy { it.id?.trim() }
    }
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
                        val user = userMap[report.userId.trim()]
                        val fullName = user?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown"
                        val email = user?.email ?: "N/A"
                        val phone = user?.mobileNumber ?: "N/A"
                        PrintableRowImpl(
                            listOf(
                                fullName,
                                email,
                                phone,
                                formatterDate(report.createdAt),
                                report.status
                            )
                        )
                    }
                    exportDynamicPDF(
                        context = context,
                        title = "Public Disturbance",
                        headers = listOf("No", "Resident", "Email", "Phone Number", "Date Created", "Status"),
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
                    navController.navigate(MainNav.CreateReport("Public Disturbance"))
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
