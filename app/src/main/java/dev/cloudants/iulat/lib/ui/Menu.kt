package dev.cloudants.iulat.lib.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.cloudants.iulat.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.cloudants.iulat.lib.components.context.MODULE
import dev.cloudants.iulat.lib.components.context.NavItem
import dev.cloudants.iulat.lib.ui.dashboard.Dashboard
import dev.cloudants.iulat.lib.ui.dashboard.ResidenceDashboard
import dev.cloudants.iulat.lib.ui.message.Message
import dev.cloudants.iulat.lib.ui.message.MessageList
import dev.cloudants.iulat.lib.ui.report.ReportList
import dev.cloudants.iulat.lib.ui.user.Account
import dev.cloudants.iulat.lib.ui.user.UsersList
import dev.cloudants.iulat.lib.viewmodels.MenuViewModel


@Composable
@Preview(showBackground = true, showSystemUi = true)
fun MenuPreview() {
    Menu(navController = rememberNavController())
}

data class SampleUserDto(val username: String, val isAdmin: Boolean = true, val isResidence: Boolean = false)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Menu(
    navController: NavController,
    viewModel: MenuViewModel = viewModel()
) {
    val userDto = SampleUserDto(username = "residence", isAdmin = true, isResidence = false)

    LaunchedEffect(Unit) {
        viewModel.setUserDefaultRoute(userDto.isAdmin, userDto.isResidence)
    }
    val routeName by remember { viewModel.routeName }
    val topBarTitle by remember { viewModel.topBarTitle }
    val navItems = getNavItems(navController, userDto)
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0049AD)
                ),
                title = {
                    Text(text = topBarTitle, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(id = R.drawable.person_circle),
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notification),
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0049AD)
            ) {
                navItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = item.icon,
                                contentDescription = null,
                                tint = if (routeName == item.routeName) Color(0xFF0049AD) else Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        selected = routeName == item.routeName,
                        onClick = {
                            viewModel.updateRoute(item.routeName)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (routeName) {
                MODULE.DASHBOARD -> Dashboard()
                MODULE.MESSAGELIST -> MessageList(navController)
                MODULE.MESSAGE -> Message(navController)
                MODULE.ACCOUNT -> Account(navController)
                MODULE.USERLIST -> UsersList(navController)
                MODULE.REPORTLIST -> ReportList()
                MODULE.RESIDENCEDASHBOARD -> ResidenceDashboard(navController)
            }
        }
    }
}

@Composable
fun getNavItems(navController: NavController, userDto: SampleUserDto): List<NavItem> {
    return when {
        userDto.isAdmin -> listOf(
            NavItem(painterResource(R.drawable.home), MODULE.DASHBOARD, navController),
            NavItem(painterResource(R.drawable.report_icon), MODULE.REPORTLIST, navController),
            NavItem(painterResource(R.drawable.users), MODULE.USERLIST, navController),
            NavItem(painterResource(R.drawable.message), MODULE.MESSAGELIST, navController),
            NavItem(painterResource(R.drawable.person), MODULE.ACCOUNT, navController),
        )
        userDto.isResidence -> listOf(
            NavItem(painterResource(R.drawable.home), MODULE.RESIDENCEDASHBOARD, navController),
            NavItem(painterResource(R.drawable.message), MODULE.MESSAGE, navController),
            NavItem(painterResource(R.drawable.person), MODULE.ACCOUNT, navController),
        )
        else -> listOf()
    }
}

