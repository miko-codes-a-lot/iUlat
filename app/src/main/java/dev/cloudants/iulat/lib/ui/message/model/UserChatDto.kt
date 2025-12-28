package dev.cloudants.iulat.lib.ui.message.model

import dev.cloudants.iulat.lib.models.entities.UserDto
import java.time.Instant

data class UserChatDto(
    val userDto: UserDto,
    val chatDto: ChatDto,
    val updatedAt: Instant?
)