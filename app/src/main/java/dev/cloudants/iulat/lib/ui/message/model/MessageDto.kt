package dev.cloudants.iulat.lib.ui.message.model

import java.time.Instant

data class MessageDto(
    val id: String?,
    val chatId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val createdAt: Instant
)
