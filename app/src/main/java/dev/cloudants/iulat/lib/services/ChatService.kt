package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.ui.message.model.ChatDto
import dev.cloudants.iulat.lib.ui.message.model.MessageDto
import dev.cloudants.iulat.lib.ui.message.model.UserChatDto
import kotlinx.coroutines.flow.Flow

interface ChatService {
    suspend fun findOneChatOrCreate(sender: UserDto, receiver: UserDto): ChatDto
    suspend fun message(sender: UserDto, receiver: UserDto, content: String): MessageDto
    fun fetchDirectMessages(sender: UserDto, receiver: UserDto): Flow<List<MessageDto>>
    fun fetchUsers(userId: String): Flow<List<UserChatDto>>
}