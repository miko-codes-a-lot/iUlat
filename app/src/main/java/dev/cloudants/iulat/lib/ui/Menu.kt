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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.cloudants.iulat.lib.components.context.MODULE
import dev.cloudants.iulat.lib.components.context.NavItem
import dev.cloudants.iulat.lib.components.context.UserSession
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.ui.dashboard.Dashboard
import dev.cloudants.iulat.lib.ui.dashboard.ResidenceDashboard
import dev.cloudants.iulat.lib.ui.message.ChatDirect
import dev.cloudants.iulat.lib.ui.message.ChatLobby
import dev.cloudants.iulat.lib.ui.notification.NotificationList
import dev.cloudants.iulat.lib.ui.report.AdminReportList
import dev.cloudants.iulat.lib.ui.report.residence_report.GarbageDisposalList
import dev.cloudants.iulat.lib.ui.user.Account
import dev.cloudants.iulat.lib.ui.user.UserDetails
import dev.cloudants.iulat.lib.ui.user.UsersList
import dev.cloudants.iulat.lib.utils.main.MainNav
import dev.cloudants.iulat.lib.viewmodels.MenuViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Menu(
    navController: NavController,
    viewModel: MenuViewModel,
    currentUser: UserDto
) {
    val context = LocalContext.current
    val currentUserState = remember { mutableStateOf<UserDto?>(currentUser) }

    val routeName by viewModel.routeName
    val topBarTitle by viewModel.topBarTitle
    val navItems = currentUserState.value?.let { getNavItems(navController, it) } ?: emptyList()

    LaunchedEffect(currentUserState.value) {
        val user = currentUserState.value
        if (user == null) {
            navController.navigate(MainNav.Login) {
                popUpTo(0)
            }
        } else {
            if (user.isResidence) {
                viewModel.updateRoute(MODULE.RESIDENCEDASHBOARD)
            } else {
                viewModel.updateRoute(MODULE.DASHBOARD)
            }
        }
    }

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

                    IconButton(
                        onClick = {
                            if (routeName == MODULE.ACCOUNT) {
                                UserSession.clearSession(context)
                                currentUserState.value = null
                                navController.navigate(MainNav.Login) {
                                    popUpTo(0)
                                }
                            } else {
                                navController.navigate(MainNav.NotificationList)
                            }
                    }) {
                        val icons = if (routeName == MODULE.ACCOUNT) {
                            R.drawable.exit
                        } else {
                            R.drawable.ic_notification
                        }
                        Icon(
                            painter = painterResource(id = icons),
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
                MODULE.CHATLOBBY -> ChatLobby(navController)
                MODULE.CHATDIRECT -> ChatDirect(navController)
                MODULE.ACCOUNT -> Account(navController)
                MODULE.USERLIST -> UsersList(navController)
                MODULE.ADMINREPORTLIST -> AdminReportList()
                MODULE.RESIDENCEDASHBOARD -> ResidenceDashboard(navController)
                MODULE.NOTIFICATIONLIST -> NotificationList(navController)
                MODULE.RESIDENCEREPORTLIST -> GarbageDisposalList(navController)
            }
        }
    }
}

@Composable
fun getNavItems(navController: NavController, userDto: UserDto): List<NavItem> {
    return when {
        userDto.isAdmin -> listOf(
            NavItem(painterResource(R.drawable.home), MODULE.DASHBOARD, navController),
            NavItem(painterResource(R.drawable.report_icon), MODULE.ADMINREPORTLIST, navController),
            NavItem(painterResource(R.drawable.users), MODULE.USERLIST, navController),
            NavItem(painterResource(R.drawable.message), MODULE.CHATLOBBY, navController),
            NavItem(painterResource(R.drawable.person), MODULE.ACCOUNT, navController),
        )
        userDto.isResidence -> listOf(
            NavItem(painterResource(R.drawable.home), MODULE.RESIDENCEDASHBOARD, navController),
            NavItem(painterResource(R.drawable.message), MODULE.CHATDIRECT, navController),
            NavItem(painterResource(R.drawable.person), MODULE.ACCOUNT, navController),
        )
        else -> listOf()
    }
}
