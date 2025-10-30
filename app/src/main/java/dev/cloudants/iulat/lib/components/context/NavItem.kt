package dev.cloudants.iulat.lib.components.context

import androidx.compose.ui.graphics.painter.Painter
import androidx.navigation.NavController

data class NavItem(
    val icon: Painter,
    val routeName: String,
    val navigation: NavController,
)