package dev.cloudants.iulat.lib.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.components.context.MODULE
import javax.inject.Inject


@HiltViewModel
class MenuViewModel @Inject constructor(
) : ViewModel() {
    var routeName = mutableStateOf(MODULE.DASHBOARD)

    val topBarTitle = mutableStateOf("Dashboard")

    fun updateRoute(newRoute: String) {
        routeName.value = newRoute
        topBarTitle.value = when (newRoute) {
            MODULE.DASHBOARD -> "Dashboard"
            MODULE.MESSAGE -> "Messages"
            MODULE.MESSAGELIST -> "Message List"
            MODULE.ACCOUNT -> "Account"
            MODULE.REPORT -> "Report"
            else -> "SUMMARY"
        }
    }
}