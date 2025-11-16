package dev.cloudants.iulat.lib.utils.main

import androidx.hilt.navigation.compose.hiltViewModel
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
import dev.cloudants.iulat.lib.ui.email.ForgotPassword
import dev.cloudants.iulat.lib.ui.message.ChatDirect
import dev.cloudants.iulat.lib.ui.message.ChatLobby
import dev.cloudants.iulat.lib.ui.message.MessageDto
import dev.cloudants.iulat.lib.ui.notification.NotificationList
import dev.cloudants.iulat.lib.ui.report.CreateReport
import dev.cloudants.iulat.lib.ui.report.EditReport
import dev.cloudants.iulat.lib.ui.report.residence_report.BrokenLightList
import dev.cloudants.iulat.lib.ui.report.residence_report.GarbageDisposalList
import dev.cloudants.iulat.lib.ui.report.residence_report.NoWaterSupplyList
import dev.cloudants.iulat.lib.ui.report.residence_report.OthersList
import dev.cloudants.iulat.lib.ui.report.residence_report.PublicDisturbanceList
import dev.cloudants.iulat.lib.ui.report.residence_report.RoadRepairList
import dev.cloudants.iulat.lib.ui.report.residence_report.RobberiesList
import dev.cloudants.iulat.lib.ui.report.residence_report.VehicleCrashesList
import dev.cloudants.iulat.lib.viewmodels.ReportViewModel
import dev.cloudants.iulat.lib.ui.user.CreateAccount
import dev.cloudants.iulat.lib.ui.user.EditAccount
import dev.cloudants.iulat.lib.ui.user.UserEdit
import dev.cloudants.iulat.lib.ui.user.UsersList
import dev.cloudants.iulat.lib.viewmodels.GarbageDisposalViewModel
import dev.cloudants.iulat.lib.viewmodels.LoginViewModel
import dev.cloudants.iulat.lib.viewmodels.MenuViewModel
import dev.cloudants.iulat.lib.viewmodels.UserViewModel
import dev.cloudants.iulat.shared.Guard

fun NavGraphBuilder.mainGraph(navController: NavController) {
    navigation<MainNav>(startDestination = MainNav.Splash) {
        composable<MainNav.Splash> {
            SplashScreen(navController)
        }

        composable<MainNav.Login> {
            val loginViewModel: LoginViewModel = hiltViewModel()
            Login(navController, loginViewModel)
        }
        composable<MainNav.Menu> {
            Guard(navController = navController) { currentUser ->
                val viewModel: MenuViewModel = hiltViewModel()
                Menu(navController, viewModel, currentUser)
            }
        }
        composable<MainNav.Dashboard> {
            Guard(navController = navController) { currentUser ->
                Dashboard()
            }
        }
        composable<MainNav.ChatDirect> {
            Guard(navController = navController) { currentUser ->
                val args = it.toRoute<MainNav.ChatDirect>()
                val userId = args.userId

                val sampleMessages = listOf(
                    MessageDto("1", userId, "2025-10-30T10:00:00.000Z"),
                    MessageDto("2", "me", "2025-10-30T10:05:00.000Z")
                )

                ChatDirect(
                    messages = sampleMessages,
                    currentUserId = "me",
                    onSendMessage = { content ->
                        println("Send message to $userId: $content")
                    }
                )
            }
        }
        composable<MainNav.Account> {
            Guard(navController = navController) { currentUser ->
                Account(navController)
            }
        }
        composable("${MainNav.EditAccount}/{settingType}"){ backStackEntry ->
            val settingType = backStackEntry.arguments?.getString("settingType") ?: ""
            Guard(navController = navController) { currentUser ->
                EditAccount(navController, settingType, currentUser)
            }
        }
        composable<MainNav.AdminReportList> {
            Guard(navController = navController) { currentUser ->
                AdminReportList()
            }
        }
        composable<MainNav.ChatLobby> {
            Guard(navController = navController) { currentUser ->
                ChatLobby(navController)
            }
        }
        composable<MainNav.ResidenceDashboard> {
            Guard(navController = navController) { currentUser ->
                ResidenceDashboard(navController)
            }
        }
        composable<MainNav.ForgotPassword> {
            Guard(navController = navController) { currentUser ->
                ForgotPassword(navController)
            }
        }
        composable<MainNav.UserList> {
            Guard(navController = navController) { currentUser ->
                UsersList(navController)
            }
        }
        composable<MainNav.CreateUser> {
            Guard(navController = navController) { currentUser ->
                CreateAccount(
                    navController = navController,
                    currentUser
                )
            }
        }

        composable<MainNav.CreateReport> {
            Guard(navController = navController) { currentUser ->
                val args = it.toRoute<MainNav.CreateReport>()
                val title = args.title
                val viewModel : ReportViewModel = hiltViewModel()
                val garbageDisposalViewModel : GarbageDisposalViewModel = hiltViewModel()
                CreateReport(
                    navController = navController,
                    reportTitle = title,
                    viewModel = viewModel,
                    currentUser = currentUser,
                    garbageDisposalViewModel = garbageDisposalViewModel
                )
            }
        }

        composable<MainNav.EditReport> {
            Guard(navController) { currentUser ->
                val args = it.toRoute<MainNav.EditReport>()
                val title = args.title
                val reportId = args.reportId
                val viewModel : ReportViewModel = hiltViewModel()
                val garbageDisposalViewModel : GarbageDisposalViewModel = hiltViewModel()
                EditReport(
                    navController = navController,
                    reportTitle = title,
                    viewModel = viewModel,
                    currentUser = currentUser,
                    garbageDisposalViewModel = garbageDisposalViewModel,
                    reportId = reportId
                )
            }
        }

        composable<MainNav.NotificationList> {
            Guard(navController = navController) { currentUser ->
                NotificationList(navController)
            }
        }

        composable<MainNav.GarbageDisposalList> {
            Guard(navController = navController) { currentUser ->
                GarbageDisposalList(navController, currentUser)
            }
        }

        composable<MainNav.PublicDisturbanceList> {
            Guard(navController = navController) { currentUser ->
                PublicDisturbanceList(navController)
            }
        }

        composable<MainNav.RobberiesList> {
            Guard(navController = navController) { currentUser ->
                RobberiesList(navController)
            }
        }

        composable<MainNav.BrokenLightList> {
            Guard(navController = navController) { currentUser ->
                BrokenLightList(navController)
            }
        }

        composable<MainNav.VehicleCrashesList> {
            Guard(navController = navController) { currentUser ->
                VehicleCrashesList(navController)
            }
        }

        composable<MainNav.RoadRepairList> {
            Guard(navController = navController) { currentUser ->
                RoadRepairList(navController)
            }
        }

        composable<MainNav.NoWaterSupplyList> {
            Guard(navController = navController) { currentUser ->
                NoWaterSupplyList(navController)
            }
        }

        composable<MainNav.OthersList> {
            Guard(navController = navController) { currentUser ->
                OthersList(navController)
            }
        }

        composable<MainNav.EditUser> {
            val args = it.toRoute<MainNav.EditUser>()
            val userViewModel : UserViewModel = hiltViewModel()
            val userDto = userViewModel.fetchUser(args.userId)
            Guard(navController = navController) { currentUser ->
                UserEdit(
                    navController = navController,
                    currentUser = currentUser,
                    userDto = userDto,
                )

            }
        }
    }
}