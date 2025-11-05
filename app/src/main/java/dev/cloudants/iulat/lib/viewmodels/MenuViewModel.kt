package dev.cloudants.iulat.lib.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.components.context.MODULE
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.services_impl.UserServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MenuViewModel @Inject constructor() : ViewModel() {
    var routeName = mutableStateOf(MODULE.DASHBOARD)
    val topBarTitle = mutableStateOf("Dashboard")

    fun updateRoute(newRoute: String) {
        routeName.value = newRoute
        topBarTitle.value = when (newRoute) {
            MODULE.DASHBOARD -> "Dashboard"
            MODULE.RESIDENCEDASHBOARD -> "Dashboard"
            MODULE.CHATDIRECT -> "Messages"
            MODULE.CHATLOBBY -> "Message List"
            MODULE.ACCOUNT -> "Account"
            MODULE.ADMINREPORTLIST -> "Report"
            MODULE.USERLIST -> "USERS LIST"
            else -> "SUMMARY"
        }
    }
}
