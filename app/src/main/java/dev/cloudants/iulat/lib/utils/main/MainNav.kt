package dev.cloudants.iulat.lib.utils.main

import kotlinx.serialization.Serializable

@Serializable
object MainNav {
    @Serializable
    object Splash

    @Serializable
    object Login

    //      ------- recompose this line -------
    @Serializable
    object Menu

    @Serializable
    object Dashboard

    @Serializable
    data class ChatDirect(val userId: String)

    @Serializable
    object Account

    @Serializable
    object AdminReportList

    @Serializable
    object ChatLobby

    @Serializable
    object ResidenceDashboard

    @Serializable
    object UserList

    @Serializable
    object CreateUser

    @Serializable
    object NotificationList

    @Serializable
    data class CreateReport (var title: String = "")

    @Serializable
    object GarbageDisposalList

    @Serializable
    object PublicDisturbanceList

    @Serializable
    object RobberiesList

    @Serializable
    object BrokenLightList

    @Serializable
    object VehicleCrashesList

    @Serializable
    object RoadRepairList

    @Serializable
    object NoWaterSupplyList

    @Serializable
    object OthersList

}