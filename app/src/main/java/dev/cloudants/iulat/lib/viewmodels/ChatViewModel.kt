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

    private val _messages = MutableStateFlow<List<MessageDto>>(emptyList())
    val messages: StateFlow<List<MessageDto>> = _messages.asStateFlow()

    private val _users = MutableStateFlow<List<UserChatDto>>(emptyList())
    val users: StateFlow<List<UserChatDto>> = _users.asStateFlow()

    fun loadDirectMessages(sender: UserDto, receiver: UserDto): Flow<List<MessageDto>> {
        return chatService.fetchDirectMessages(sender, receiver)
    }

    fun startMessageFlow(sender: UserDto, receiver: UserDto) {
        viewModelScope.launch {
            chatService.fetchDirectMessages(sender, receiver)
                .collect { fetchedMessages ->
                    _messages.value = fetchedMessages.asReversed()
                }
        }
    }

    fun sendMessage(sender: UserDto, receiver: UserDto, content: String) {
        viewModelScope.launch {
            val msg = chatService.message(sender, receiver, content)
            _messages.update { listOf(msg) + it }
        }
    }

    fun loadUsers(userId: String) {
        viewModelScope.launch {
            chatService.fetchUsers(userId)
                .collect { _users.value = it }
        }
    }

    suspend fun getOrCreateChat(sender: UserDto, receiver: UserDto): ChatDto {
        return chatService.findOneChatOrCreate(sender, receiver)
    }
}