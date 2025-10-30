package dev.cloudants.iulat.lib.intent

sealed class LoginIntent {
    data class DisplayDialog(val isShow: Boolean = false)
}