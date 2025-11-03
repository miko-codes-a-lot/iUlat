package dev.cloudants.iulat.lib.utils.main

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import dev.cloudants.iulat.lib.ui.user.Account
import dev.cloudants.iulat.lib.ui.dashboard.Dashboard
import dev.cloudants.iulat.lib.ui.Login
import dev.cloudants.iulat.lib.ui.Menu
import dev.cloudants.iulat.lib.ui.report.AdminReportList
import dev.cloudants.iulat.lib.ui.SplashScreen
import dev.cloudants.iulat.lib.ui.dashboard.ResidenceDashboard
import dev.cloudants.iulat.lib.ui.message.ChatDirect
import dev.cloudants.iulat.lib.ui.message.ChatLobby
import dev.cloudants.iulat.lib.ui.message.MessageDto
import dev.cloudants.iulat.lib.ui.notification.NotificationList
import dev.cloudants.iulat.lib.ui.report.CreateReport
import dev.cloudants.iulat.lib.ui.report.residence_report.BrokenLightList
import dev.cloudants.iulat.lib.ui.report.residence_report.GarbageDisposalList
import dev.cloudants.iulat.lib.ui.report.residence_report.NoWaterSupplyList
import dev.cloudants.iulat.lib.ui.report.residence_report.OthersList
import dev.cloudants.iulat.lib.ui.report.residence_report.PublicDisturbanceList
import dev.cloudants.iulat.lib.ui.report.residence_report.RoadRepairList
import dev.cloudants.iulat.lib.ui.report.residence_report.RobberiesList
import dev.cloudants.iulat.lib.ui.report.residence_report.VehicleCrashesList
import dev.cloudants.iulat.lib.ui.report.viewmodel.ReportViewModel
import dev.cloudants.iulat.lib.ui.user.CreateAccount
import dev.cloudants.iulat.lib.ui.user.UsersList
import dev.cloudants.iulat.lib.viewmodels.LoginViewModel

fun NavGraphBuilder.mainGraph(navController: NavController) {
    navigation<MainNav>(startDestination = MainNav.Splash) {
        composable<MainNav.Splash> {
            SplashScreen(navController)
        }

        composable<MainNav.Login> {
            val loginViewModel: LoginViewModel = viewModel()
            Login(navController, loginViewModel)
        }
        composable<MainNav.Menu> {
            Menu(navController)
        }
        composable<MainNav.Dashboard> {
            Dashboard()
        }
        composable<MainNav.ChatDirect> {
            val args = it.toRoute<MainNav.ChatDirect>()
            val userId = args.userId

            val sampleMessages = listOf(
                MessageDto("1", userId,  "2025-10-30T10:00:00.000Z"),
                MessageDto("2", "me",  "2025-10-30T10:05:00.000Z")
            )

            ChatDirect(
                messages = sampleMessages,
                currentUserId = "me",
                onSendMessage = { content ->
                    println("Send message to $userId: $content")
                }
            )
        }
        composable<MainNav.Account> {
            Account(navController)
        }
        composable<MainNav.AdminReportList> {
            AdminReportList()
        }
        composable<MainNav.ChatLobby> {
            ChatLobby(navController)
        }
        composable<MainNav.ResidenceDashboard> {
            ResidenceDashboard(navController)
        }
        composable<MainNav.UserList> {
            UsersList(navController)
        }
        composable<MainNav.CreateUser> {
            CreateAccount()
        }

        composable<MainNav.CreateReport> {
            val args = it.toRoute<MainNav.CreateReport>()
            val title = args.title
            val viewModel : ReportViewModel = viewModel()
            CreateReport(navController, reportTitle = title, viewModel = viewModel)
        }
        composable<MainNav.NotificationList> {
            NotificationList(navController)
        }

        composable<MainNav.GarbageDisposalList> {
            GarbageDisposalList(navController)
        }

        composable<MainNav.PublicDisturbanceList> {
            PublicDisturbanceList(navController)
        }

        composable<MainNav.RobberiesList> {
            RobberiesList(navController)
        }

        composable<MainNav.BrokenLightList> {
            BrokenLightList(navController)
        }

        composable<MainNav.VehicleCrashesList> {
            VehicleCrashesList(navController)
        }

        composable<MainNav.RoadRepairList> {
            RoadRepairList(navController)
        }

        composable<MainNav.NoWaterSupplyList> {
            NoWaterSupplyList(navController)
        }

        composable<MainNav.OthersList> {
            OthersList(navController)
        }

    }
}