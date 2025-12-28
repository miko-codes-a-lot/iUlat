package dev.cloudants.iulat.lib.viewmodels

import kotlin.collections.map
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.models.entities.NotificationItem
import dev.cloudants.iulat.lib.models.entities.toNotificationItem
import dev.cloudants.iulat.lib.services.NotificationService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

//interface SessionManager {
//    val currentUserId: String
//}

@HiltViewModel
class NotificationViewModel @Inject constructor(
    val notificationService: NotificationService,
//    val sessionManager: SessionManager
) : ViewModel() {
//    val notificationUiState: StateFlow<List<NotificationItem>> =
//        notificationService.getNotificationsStream(sessionManager.currentUserId)
//            .map { notifyDtos ->
//                notifyDtos.map { it.toNotificationItem() }
//            }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(5000),
//                initialValue = emptyList()
//            )
    private val userId = "current_user_id"
    val notificationUiState: StateFlow<List<NotificationItem>> =
        notificationService.getNotificationsStream("currentUser123")
            .map { notifyDtos ->
                notifyDtos.map { it.toNotificationItem() }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    fun onNotificationClicked(notificationId: String) {
        viewModelScope.launch {
            try {
                notificationService.markAsRead(notificationId)
            } catch (e: Exception) {
                Log.e("NotifVM", "Failed to mark notification $notificationId as read", e)
            }
        }
    }

    fun navigateToReport(notification: NotificationItem) {
        Log.d("NotifVM", "Navigate to report: ${notification.reportType} with ID ${notification.reportId}")
    }
}