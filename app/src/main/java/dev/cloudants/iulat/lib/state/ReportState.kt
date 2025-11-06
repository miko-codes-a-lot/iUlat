package dev.cloudants.iulat.lib.state

data class ReportState(
    val isDialogVisible: Boolean = false,
    val reportText: String = "",
    val imageUri: String? = null
)