package dev.cloudants.iulat.lib.utils.main

import kotlinx.serialization.Serializable

@Serializable
object MainNav {
    @Serializable
    object Splash

    @Serializable
    object Login

    @Serializable
    data class TokenVerification(val email: String)

    @Serializable
    data class ResetPassword(val email: String, val passwordToken: String)

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
    object EditAccount

    @Serializable
    object AdminReportList

    @Serializable
    object ChatLobby

    @Serializable
    object ResidenceDashboard

    @Serializable
    object UserList

    @Serializable
    object ForgotPassword

    @Serializable
    object CreateUser

    @Serializable
    object NotificationList

    @Serializable
    data class CreateReport (var title: String = "")

    @Serializable
    data class EditReport (var title: String = "", var reportId: String)


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

    @Serializable
    data class EditUser(val userId: String)

    @Serializable
    data class Map(var addressId: String)

    @Serializable
    data class ViewReport (var title: String = "", var reportId: String)

}