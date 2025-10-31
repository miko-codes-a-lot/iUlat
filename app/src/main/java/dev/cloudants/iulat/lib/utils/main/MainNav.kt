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
    object Message

    @Serializable
    object Account

    @Serializable
    object Report

    @Serializable
    object MessageList

    @Serializable
    object ResidenceDashboard

    @Serializable
    data class CreateReport (var title: String = "")

}