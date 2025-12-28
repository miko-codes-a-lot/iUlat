package dev.cloudants.iulat.lib.models.entities
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class NotifyDto(
    var id: String? = null,
    val type: String = "NotifyDto",
    val sender: String,
    val receiver: String,
    val documentId: String? = null,
    val documentType: String? = null,
    val message: String = "",
    var read: Boolean = false,
    val createdAt: Instant? = null,
)

data class NotificationItem(
    val id: String,
    val message: String,
    val isRead: Boolean,
    val reportId: String?,
    val reportType: String?,
    val createdAt: Instant?,
)

fun NotifyDto.toNotificationItem(): NotificationItem {
    return NotificationItem(
        id = this.id ?: throw IllegalStateException("NotifyDto must have an ID for UI."),
        message = this.message,
        isRead = this.read,
        reportId = this.documentId,
        reportType = this.documentType,
        createdAt = this.createdAt
    )
}