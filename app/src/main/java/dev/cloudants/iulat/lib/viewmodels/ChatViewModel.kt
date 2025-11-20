package dev.cloudants.iulat.lib.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.services.ChatService
import dev.cloudants.iulat.lib.ui.message.model.ChatDto
import dev.cloudants.iulat.lib.ui.message.model.MessageDto
import dev.cloudants.iulat.lib.ui.message.model.UserChatDto
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatService: ChatService
) : ViewModel() {
    private val _users = MutableStateFlow<List<UserChatDto>>(emptyList())
    val users: StateFlow<List<UserChatDto>> = _users.asStateFlow()

    fun fetchDirectMessages(sender: UserDto, receiver: UserDto) : Flow<List<MessageDto>> {
        return chatService.fetchDirectMessages(sender, receiver)
    }

    suspend fun sendMessage(sender: UserDto, receiver: UserDto, content: String) : Result<MessageDto> {
        return chatService.message(sender, receiver, content)
    }

    fun fetchUsers(userId: String) {
        viewModelScope.launch {
            chatService.fetchUsers(userId)
                .collect { _users.value = it }
        }
    }

    suspend fun findOneChatOrCreate(sender: UserDto, receiver: UserDto): ChatDto {
        return chatService.findOneChatOrCreate(sender, receiver)
    }
}