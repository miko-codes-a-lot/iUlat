package dev.cloudants.iulat.lib.viewmodels

import kotlin.collections.map
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.models.entities.NotificationItem
import dev.cloudants.iulat.lib.models.entities.toNotificationItem
import dev.cloudants.iulat.lib.services.NotificationService
import dev.cloudants.iulat.shared.SessionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationService: NotificationService,
    private val sessionManager: SessionManager
) : ViewModel() {
    val notificationUiState: StateFlow<List<NotificationItem>> = sessionManager.userIdFlow
        .flatMapLatest { userId ->
            if (userId == null) flowOf(emptyList())
            else notificationService.getNotificationsStream(userId)
        }
        .map { list -> list.map { it.toNotificationItem() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hasUnreadNotifications: StateFlow<Boolean> = notificationUiState
        .map { notifications -> notifications.any { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    fun onNotificationClicked(id: String) {
        viewModelScope.launch {
            notificationService.markAsRead(id)
        }
    }
}