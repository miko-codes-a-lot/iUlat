package dev.cloudants.iulat.lib.intent

sealed class AccountIntent {
    data class AccountDialog(val isShow: Boolean = false) : AccountIntent()
}