package dev.cloudants.iulat.lib.services
import dev.cloudants.iulat.lib.models.entities.NotifyDto
import kotlinx.coroutines.flow.Flow

interface NotificationService {
    fun getNotificationsStream(userId: String): Flow<List<NotifyDto>>
    suspend fun markAsRead(notificationId: String)
}