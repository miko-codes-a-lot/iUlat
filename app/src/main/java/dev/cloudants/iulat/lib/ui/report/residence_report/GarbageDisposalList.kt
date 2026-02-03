package dev.cloudants.iulat.lib.ui.report.residence_report

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.itextpdf.text.Image
import dev.cloudants.iulat.MainActivity
import dev.cloudants.iulat.R
import dev.cloudants.iulat.lib.components.context.MODULE
import dev.cloudants.iulat.lib.components.context.PrintableRowImpl
import dev.cloudants.iulat.lib.components.context.formatterDate
import dev.cloudants.iulat.lib.components.context.formatterToFilterMonth
import dev.cloudants.iulat.lib.components.header.CustomHeader
import dev.cloudants.iulat.lib.components.print.Print
import dev.cloudants.iulat.lib.components.print.exportDynamicPDF
import dev.cloudants.iulat.lib.models.entities.GarbageDisposalDto
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.ui.report.MonthFilterCard
import dev.cloudants.iulat.lib.utils.main.MainNav
import dev.cloudants.iulat.lib.viewmodels.GarbageDisposalViewModel
import dev.cloudants.iulat.lib.viewmodels.UserViewModel
import java.util.Base64


@Composable
fun GarbageDisposalList(
    navController: NavController,
    currentUser: UserDto
) {
    val garbageDisposalViewModel: GarbageDisposalViewModel = hiltViewModel()
    val state by garbageDisposalViewModel.state.collectAsState()
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
            garbageDisposalViewModel.fetchAll(currentUser.id!!)
        } else {
            garbageDisposalViewModel.fetchAllGarbageReports()
        }
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)),
        floatingActionButton = {
            FloatingGarbageDisposalRecordIcon(
                navController,
                currentUser,
                context,
                items = filteredItems,
                users = users
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
            CustomHeader(MODULE.GARBAGE_DISPOSAL)
            MonthFilterCard(
                selectedMonth = selectedMonth,
                onMonthSelected = { selectedMonth = it }
            )
            if (state.isLoading) {
                CircularProgressIndicator(color = Color(0xFF0049AD))
            }

            GarbageListContainer(
                navController = navController,
                items = filteredItems
            )
        }
    }
}

@Composable
fun GarbageListContainer(
    navController: NavController,
    items: List<GarbageDisposalDto>
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
                    text = "No garbage disposal reports found.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        items(items) { report ->
            GarbageDisposalItem(navController, report)
        }
    }
}


@Composable
fun GarbageDisposalItem(
    navController: NavController,
    report: GarbageDisposalDto,

) {
    ElevatedButton(
        onClick = {
            report.id?.let { id ->
                navController.navigate(MainNav.ViewPendingReport("Garbage Disposal", id))
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
fun FloatingGarbageDisposalRecordIcon(
    navController: NavController,
    currentUser: UserDto,
    context: Context,
    items: List<GarbageDisposalDto>,
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
                        Log.e("ID :: ", report.id!!)
                        Log.e("Email :: ", report.email ?: "N/A")
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
                        title = "Garbage Disposal Report",
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
                    navController.navigate(MainNav.CreateReport("Garbage Disposal"))
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
