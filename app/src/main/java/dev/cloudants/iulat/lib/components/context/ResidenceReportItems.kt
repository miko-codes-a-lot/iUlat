package dev.cloudants.iulat.lib.components.context

import androidx.navigation.NavController

data class ResidenceReportItems(
    val iconRes: Int,
    val navigation: NavController,
    val title: String,
    val routeName: String,
)