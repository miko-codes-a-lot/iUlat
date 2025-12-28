package dev.cloudants.iulat.lib.ui.message.model

import java.time.Instant

data class ChatDto(
    val id: String,
    val user1Id: String,
    val user2Id: String,
    val lastMessage: String,
    val isRead: Boolean,
    val updatedAt: Instant,
)