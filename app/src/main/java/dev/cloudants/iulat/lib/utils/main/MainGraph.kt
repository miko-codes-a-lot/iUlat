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
import dev.cloudants.iulat.lib.ui.message.Message
import dev.cloudants.iulat.lib.ui.message.MessageList
import dev.cloudants.iulat.lib.ui.report.ReportList
import dev.cloudants.iulat.lib.ui.SplashScreen
import dev.cloudants.iulat.lib.ui.dashboard.ResidenceDashboard
import dev.cloudants.iulat.lib.ui.report.CreateReport
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
        composable<MainNav.Message> {
            Message(navController)
        }
        composable<MainNav.Account> {
            Account(navController)
        }
        composable<MainNav.Report> {
            ReportList()
        }
        composable<MainNav.MessageList> {
            MessageList(navController)
        }
        composable<MainNav.ResidenceDashboard> {
            ResidenceDashboard(navController)
        }

        composable<MainNav.CreateReport> {
            val args = it.toRoute<MainNav.CreateReport>()
            val title = args.title
            CreateReport(navController, reportTitle = title)
        }

    }
}