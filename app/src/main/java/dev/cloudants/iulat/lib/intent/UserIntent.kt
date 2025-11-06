package dev.cloudants.iulat.lib.intent

import dev.cloudants.iulat.lib.models.entities.UserDto

sealed class UserIntent {
    data class CreateUser(val user: UserDto) : UserIntent()
    object ClearState : UserIntent()
}