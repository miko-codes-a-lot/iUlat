package dev.cloudants.iulat.lib.utils.main

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import dev.cloudants.iulat.lib.components.context.MapReportData
import dev.cloudants.iulat.lib.ui.user.Account
import dev.cloudants.iulat.lib.ui.dashboard.AdminDashboard
import dev.cloudants.iulat.lib.ui.Login
import dev.cloudants.iulat.lib.ui.Menu
import dev.cloudants.iulat.lib.ui.report.AdminReportList
import dev.cloudants.iulat.lib.ui.SplashScreen
import dev.cloudants.iulat.lib.ui.dashboard.ResidenceDashboard
import dev.cloudants.iulat.lib.ui.email.ForgotPassword
import dev.cloudants.iulat.lib.ui.email.ResetPassword
import dev.cloudants.iulat.lib.ui.email.TokenVerification
import dev.cloudants.iulat.lib.ui.map.GlobalMapUI
import dev.cloudants.iulat.lib.ui.map.MapUI
import dev.cloudants.iulat.lib.ui.message.ChatDirect
import dev.cloudants.iulat.lib.ui.message.ChatLobby
import dev.cloudants.iulat.lib.ui.notification.NotificationList
import dev.cloudants.iulat.lib.ui.report.CreateReport
import dev.cloudants.iulat.lib.ui.report.EditReport
import dev.cloudants.iulat.lib.ui.report.NotificationReportVIew
import dev.cloudants.iulat.lib.ui.report.ViewRejReport
import dev.cloudants.iulat.lib.ui.report.ViewReport
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
import dev.cloudants.iulat.lib.viewmodels.AdminReportViewModel
import dev.cloudants.iulat.lib.viewmodels.BrokenStreetLightViewModel
import dev.cloudants.iulat.lib.viewmodels.ChatViewModel
import dev.cloudants.iulat.lib.viewmodels.GarbageDisposalViewModel
import dev.cloudants.iulat.lib.viewmodels.LoginViewModel
import dev.cloudants.iulat.lib.viewmodels.MenuViewModel
import dev.cloudants.iulat.lib.viewmodels.NoWaterSupplyViewModel
import dev.cloudants.iulat.lib.viewmodels.OthersViewModel
import dev.cloudants.iulat.lib.viewmodels.PublicDisturbanceViewModel
import dev.cloudants.iulat.lib.viewmodels.RoadRepairViewModel
import dev.cloudants.iulat.lib.viewmodels.RobberiesViewModel
import dev.cloudants.iulat.lib.viewmodels.UserViewModel
import dev.cloudants.iulat.lib.viewmodels.VehicleCrashViewModel
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

        composable<MainNav.TokenVerification> {
            val args = it.toRoute<MainNav.TokenVerification>()
            val userViewModel: UserViewModel = hiltViewModel()
            val userDto = userViewModel.fetchByEmail(args.email)
            if (userDto != null) {
                TokenVerification(email = userDto.email!!, navController = navController)
            }
        }

        composable<MainNav.ResetPassword> {
            val args = it.toRoute<MainNav.ResetPassword>()
            ResetPassword(email = args.email!!, token = args.passwordToken!!, navController = navController)
        }

        composable<MainNav.Menu> {
            Guard(navController = navController) { currentUser ->
                val viewModel: MenuViewModel = hiltViewModel()
                Menu(navController, viewModel, currentUser)
            }
        }
        composable<MainNav.Dashboard> {
            Guard(navController = navController) { currentUser ->

                AdminDashboard(
                    navController = navController,
                    currentUser
                )
            }
        }
        composable<MainNav.ChatDirect> {
            val args = it.toRoute<MainNav.ChatDirect>()
            Guard(navController = navController) { currentUser ->
                val chatViewModel: ChatViewModel = hiltViewModel()
                val isChatReady = remember { mutableStateOf(false) }
                val userViewModel: UserViewModel = hiltViewModel()
                val receiver = userViewModel.fetchUser(args.userId)

                LaunchedEffect(key1 = "message") {
                    chatViewModel.findOneChatOrCreate(currentUser, receiver)
                    isChatReady.value = true
                }
                val messages = if (isChatReady.value) {
                    chatViewModel.fetchDirectMessages(currentUser, receiver)
                        .collectAsStateWithLifecycle(
                            initialValue = emptyList()
                        ).value
                } else {
                    emptyList()
                }
                ChatDirect(
                    messages = messages,
                    currentUserId = currentUser.id!!,
                    onSendMessage = { message ->
                        chatViewModel.sendMessage(currentUser, receiver, message)
                    }
                )
            }
        }
        composable<MainNav.Account> {
            Guard(navController = navController) { currentUser ->
                Account(navController, currentUser)
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
                AdminReportList(navController = navController)
            }
        }
        composable<MainNav.ChatLobby> {
            Guard(navController = navController) { currentUser ->
                ChatLobby(
                    navController = navController,
                    currentUser.id!!
                )
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
                val arg = it.toRoute<MainNav.CreateUser>()
                val stringSource = arg.source
                CreateAccount(
                    navController = navController,
                    currentUser,
                    stringSource = stringSource
                )
            }
        }

        composable<MainNav.CreateReport> {
            Guard(navController = navController) { currentUser ->
                val args = it.toRoute<MainNav.CreateReport>()
                val title = args.title
                val viewModel : ReportViewModel = hiltViewModel()
                val garbageDisposalViewModel : GarbageDisposalViewModel = hiltViewModel()
                val publicDisturbanceViewModel : PublicDisturbanceViewModel = hiltViewModel()
                val robberiesViewModel : RobberiesViewModel = hiltViewModel()
                val brokenStreetLightViewModel : BrokenStreetLightViewModel = hiltViewModel()
                val vehicleCrashViewModel : VehicleCrashViewModel = hiltViewModel()
                val roadRepairViewModel : RoadRepairViewModel = hiltViewModel()
                val noWaterSupplyViewModel : NoWaterSupplyViewModel = hiltViewModel()
                val othersViewModel : OthersViewModel = hiltViewModel()
                CreateReport(
                    navController = navController,
                    reportTitle = title,
                    viewModel = viewModel,
                    currentUser = currentUser,
                    garbageDisposalViewModel = garbageDisposalViewModel,
                    publicDisturbanceViewModel = publicDisturbanceViewModel,
                    robberiesViewModel = robberiesViewModel,
                    brokenStreetLightViewModel = brokenStreetLightViewModel,
                    vehicleCrashViewModel = vehicleCrashViewModel,
                    roadRepairViewModel = roadRepairViewModel,
                    noWaterSupplyViewModel = noWaterSupplyViewModel,
                    othersViewModel = othersViewModel,
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
                val publicDisturbanceViewModel : PublicDisturbanceViewModel = hiltViewModel()
                val robberiesViewModel : RobberiesViewModel = hiltViewModel()
                val brokenStreetLightViewModel : BrokenStreetLightViewModel = hiltViewModel()
                val vehicleCrashViewModel : VehicleCrashViewModel = hiltViewModel()
                val roadRepairViewModel : RoadRepairViewModel = hiltViewModel()
                val noWaterSupplyViewModel : NoWaterSupplyViewModel = hiltViewModel()
                val othersViewModel : OthersViewModel = hiltViewModel()
                EditReport(
                    navController = navController,
                    reportTitle = title,
                    viewModel = viewModel,
                    currentUser = currentUser,
                    garbageDisposalViewModel = garbageDisposalViewModel,
                    publicDisturbanceViewModel = publicDisturbanceViewModel,
                    robberiesViewModel = robberiesViewModel,
                    brokenStreetLightViewModel = brokenStreetLightViewModel,
                    vehicleCrashViewModel = vehicleCrashViewModel,
                    roadRepairViewModel = roadRepairViewModel,
                    noWaterSupplyViewModel = noWaterSupplyViewModel,
                    othersViewModel = othersViewModel,
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
                PublicDisturbanceList(
                    navController,
                    currentUser
                )
            }
        }

        composable<MainNav.RobberiesList> {
            Guard(navController = navController) { currentUser ->
                RobberiesList(
                    navController,
                    currentUser
                )
            }
        }

        composable<MainNav.BrokenLightList> {
            Guard(navController = navController) { currentUser ->
                BrokenLightList(
                    navController,
                    currentUser = currentUser
                )
            }
        }

        composable<MainNav.VehicleCrashesList> {
            Guard(navController = navController) { currentUser ->
                VehicleCrashesList(
                    navController,
                    currentUser = currentUser
                )
            }
        }

        composable<MainNav.RoadRepairList> {
            Guard(navController = navController) { currentUser ->
                RoadRepairList(
                    navController,
                    currentUser
                )
            }
        }

        composable<MainNav.NoWaterSupplyList> {
            Guard(navController = navController) { currentUser ->
                NoWaterSupplyList(
                    navController,
                    currentUser
                )
            }
        }

        composable<MainNav.OthersList> {
            Guard(navController = navController) { currentUser ->
                OthersList(
                    navController,
                    currentUser
                )
            }
        }

        composable<MainNav.EditUser> {
            val args = it.toRoute<MainNav.EditUser>()
            val userViewModel : UserViewModel = hiltViewModel()
            val userDto = userViewModel.fetchUser(args.userId)
            val stringSource = args.source
            Guard(navController = navController) { currentUser ->
                UserEdit(
                    navController = navController,
                    currentUser = currentUser,
                    userDto = userDto,
                    stringSource = stringSource
                )

            }
        }

        composable<MainNav.Map> {
            val args = it.toRoute<MainNav.Map>()
            val publicDisturbanceViewModel: PublicDisturbanceViewModel = hiltViewModel()
            val garbageViewModel: GarbageDisposalViewModel = hiltViewModel()
            val robberyViewModel: RobberiesViewModel = hiltViewModel()
            val streetLightViewModel: BrokenStreetLightViewModel = hiltViewModel()
            val vehicleViewModel: VehicleCrashViewModel = hiltViewModel()
            val roadViewModel: RoadRepairViewModel = hiltViewModel()
            val waterViewModel: NoWaterSupplyViewModel = hiltViewModel()
            val othersViewModel: OthersViewModel = hiltViewModel()

            LaunchedEffect(args.reportType) {
                when (args.reportType) {
                    "Public Disturbance" -> publicDisturbanceViewModel.fetchAllPublicDisturbance()
                    "Garbage Disposal" -> garbageViewModel.fetchAllGarbageReports()
                    "Robberies" -> robberyViewModel.fetchAllRobberies()
                    "Broken Streetlights" -> streetLightViewModel.fetchAllBrokenStreet()
                    "Vehicle Crashes" -> vehicleViewModel.fetchAllVehicleCrash()
                    "Road Repair" -> roadViewModel.fetchAllRoadRepair()
                    "No Water Supply" -> waterViewModel.fetchAllNoWater()
                    else -> othersViewModel.fetchAllOthers()
                }
            }

            val selectedReport = when (args.reportType) {
                "Public Disturbance" -> {
                    publicDisturbanceViewModel.state.collectAsState().value.items
                        .find { it.id == args.addressId }
                        ?.let { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "Public Disturbance") }
                }
                "Garbage Disposal" -> {
                    garbageViewModel.state.collectAsState().value.items
                        .find { it.id == args.addressId }
                        ?.let { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "Garbage Disposal") }
                }
                "Robberies" -> {
                    robberyViewModel.state.collectAsState().value.items
                        .find { it.id == args.addressId }
                        ?.let { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "Robberies") }
                }
                "Broken Streetlights" -> {
                    streetLightViewModel.state.collectAsState().value.items
                        .find { it.id == args.addressId }
                        ?.let { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "Broken Streetlights") }
                }
                "Vehicle Crashes" -> {
                    vehicleViewModel.state.collectAsState().value.items
                        .find { it.id == args.addressId }
                        ?.let { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "Vehicle Crashes") }
                } "Road Repair" -> {
                    roadViewModel.state.collectAsState().value.items
                        .find { it.id == args.addressId }
                        ?.let { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "Road Repair") }
                } "No Water Supply" -> {
                    waterViewModel.state.collectAsState().value.items
                        .find { it.id == args.addressId }
                        ?.let { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "No Water Supply") }
                }
                else -> {
                    othersViewModel.state.collectAsState().value.items
                        .find { it.id == args.addressId }
                        ?.let { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "Others") }
                }
            }
            Guard(navController = navController) {
                MapUI(navController = navController, reportData = selectedReport)
            }
        }

        composable<MainNav.ViewReport> {
            Guard(navController) { currentUser ->
                val args = it.toRoute<MainNav.ViewReport>()
                val title = args.title
                val reportId = args.reportId
                val viewModel : ReportViewModel = hiltViewModel()
                val garbageDisposalViewModel : GarbageDisposalViewModel = hiltViewModel()
                val publicDisturbanceViewModel : PublicDisturbanceViewModel = hiltViewModel()
                val robberiesViewModel : RobberiesViewModel = hiltViewModel()
                val brokenStreetLightViewModel : BrokenStreetLightViewModel = hiltViewModel()
                val vehicleCrashViewModel : VehicleCrashViewModel = hiltViewModel()
                val roadRepairViewModel : RoadRepairViewModel = hiltViewModel()
                val noWaterSupplyViewModel : NoWaterSupplyViewModel = hiltViewModel()
                val othersViewModel : OthersViewModel = hiltViewModel()
                val adminViewModel: AdminReportViewModel = hiltViewModel()
                ViewReport(
                    navController = navController,
                    reportTitle = title,
                    viewModel = viewModel,
                    currentUser = currentUser,
                    garbageDisposalViewModel = garbageDisposalViewModel,
                    publicDisturbanceViewModel = publicDisturbanceViewModel,
                    robberiesViewModel = robberiesViewModel,
                    brokenStreetLightViewModel = brokenStreetLightViewModel,
                    vehicleCrashViewModel = vehicleCrashViewModel,
                    roadRepairViewModel = roadRepairViewModel,
                    noWaterSupplyViewModel = noWaterSupplyViewModel,
                    othersViewModel = othersViewModel,
                    reportId = reportId,
                    adminViewModel = adminViewModel
                )
            }
        }

        composable<MainNav.NotificationReportVIew> {
            Guard(navController) { currentUser ->
                val args = it.toRoute<MainNav.ViewReport>()
                val title = args.title
                val reportId = args.reportId
                val viewModel : ReportViewModel = hiltViewModel()
                val garbageDisposalViewModel : GarbageDisposalViewModel = hiltViewModel()
                val publicDisturbanceViewModel : PublicDisturbanceViewModel = hiltViewModel()
                val robberiesViewModel : RobberiesViewModel = hiltViewModel()
                val brokenStreetLightViewModel : BrokenStreetLightViewModel = hiltViewModel()
                val vehicleCrashViewModel : VehicleCrashViewModel = hiltViewModel()
                val roadRepairViewModel : RoadRepairViewModel = hiltViewModel()
                val noWaterSupplyViewModel : NoWaterSupplyViewModel = hiltViewModel()
                val othersViewModel : OthersViewModel = hiltViewModel()
                val adminViewModel: AdminReportViewModel = hiltViewModel()
                NotificationReportVIew(
                    navController = navController,
                    reportTitle = title,
                    viewModel = viewModel,
                    currentUser = currentUser,
                    garbageDisposalViewModel = garbageDisposalViewModel,
                    publicDisturbanceViewModel = publicDisturbanceViewModel,
                    robberiesViewModel = robberiesViewModel,
                    brokenStreetLightViewModel = brokenStreetLightViewModel,
                    vehicleCrashViewModel = vehicleCrashViewModel,
                    roadRepairViewModel = roadRepairViewModel,
                    noWaterSupplyViewModel = noWaterSupplyViewModel,
                    othersViewModel = othersViewModel,
                    reportId = reportId,
                    adminViewModel = adminViewModel,
                )
            }
        }

        composable<MainNav.MapReports> {
            Guard(navController) { currentUser ->
                val publicDisturbanceViewModel: PublicDisturbanceViewModel = hiltViewModel()
                val garbageViewModel: GarbageDisposalViewModel = hiltViewModel()
                val robberyViewModel: RobberiesViewModel = hiltViewModel()
                val streetLightViewModel: BrokenStreetLightViewModel = hiltViewModel()
                val vehicleViewModel: VehicleCrashViewModel = hiltViewModel()
                val roadViewModel: RoadRepairViewModel = hiltViewModel()
                val waterViewModel: NoWaterSupplyViewModel = hiltViewModel()
                val othersViewModel: OthersViewModel = hiltViewModel()

                LaunchedEffect(Unit) {
                    publicDisturbanceViewModel.fetchAllPublicDisturbance()
                    garbageViewModel.fetchAllGarbageReports()
                    robberyViewModel.fetchAllRobberies()
                    streetLightViewModel.fetchAllBrokenStreet()
                    vehicleViewModel.fetchAllVehicleCrash()
                    roadViewModel.fetchAllRoadRepair()
                    waterViewModel.fetchAllNoWater()
                    othersViewModel.fetchAllOthers()
                }

                val publicState by publicDisturbanceViewModel.state.collectAsState()
                val garbageState by garbageViewModel.state.collectAsState()
                val robberyState by robberyViewModel.state.collectAsState()
                val streetLightState by streetLightViewModel.state.collectAsState()
                val vehicleState by vehicleViewModel.state.collectAsState()
                val roadState by roadViewModel.state.collectAsState()
                val waterState by waterViewModel.state.collectAsState()
                val othersState by othersViewModel.state.collectAsState()

                val allMapPoints = remember(
                    publicState, garbageState, robberyState, streetLightState,
                    vehicleState, roadState, waterState, othersState
                ) {
                    val combinedList = mutableListOf<MapReportData>()
                    combinedList.addAll(publicState.items.map { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "Public Disturbance") })
                    combinedList.addAll(garbageState.items.map { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "Garbage Disposal") })
                    combinedList.addAll(robberyState.items.map { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "Robberies") })
                    combinedList.addAll(streetLightState.items.map { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "Broken Streetlights") })
                    combinedList.addAll(vehicleState.items.map { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "Vehicle Crashes") })
                    combinedList.addAll(roadState.items.map { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "Road Repair") })
                    combinedList.addAll(waterState.items.map { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "No Water Supply") })
                    combinedList.addAll(othersState.items.map { MapReportData(it.id, it.latitude, it.longitude, it.reportDetails, "Others") })
                    combinedList
                }

                GlobalMapUI(
                    navController = navController,
                    reportList = allMapPoints,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable<MainNav.ViewPendingReport> {
            Guard(navController) { currentUser ->
                val args = it.toRoute<MainNav.ViewPendingReport>()
                val title = args.title
                val reportId = args.reportId
                val viewModel : ReportViewModel = hiltViewModel()
                val garbageDisposalViewModel : GarbageDisposalViewModel = hiltViewModel()
                val publicDisturbanceViewModel : PublicDisturbanceViewModel = hiltViewModel()
                val robberiesViewModel : RobberiesViewModel = hiltViewModel()
                val brokenStreetLightViewModel : BrokenStreetLightViewModel = hiltViewModel()
                val vehicleCrashViewModel : VehicleCrashViewModel = hiltViewModel()
                val roadRepairViewModel : RoadRepairViewModel = hiltViewModel()
                val noWaterSupplyViewModel : NoWaterSupplyViewModel = hiltViewModel()
                val othersViewModel : OthersViewModel = hiltViewModel()
                val adminViewModel: AdminReportViewModel = hiltViewModel()
                ViewRejReport(
                    navController = navController,
                    reportTitle = title,
                    viewModel = viewModel,
                    currentUser = currentUser,
                    garbageDisposalViewModel = garbageDisposalViewModel,
                    publicDisturbanceViewModel = publicDisturbanceViewModel,
                    robberiesViewModel = robberiesViewModel,
                    brokenStreetLightViewModel = brokenStreetLightViewModel,
                    vehicleCrashViewModel = vehicleCrashViewModel,
                    roadRepairViewModel = roadRepairViewModel,
                    noWaterSupplyViewModel = noWaterSupplyViewModel,
                    othersViewModel = othersViewModel,
                    reportId = reportId,
                    adminViewModel = adminViewModel
                )
            }
        }
    }
}