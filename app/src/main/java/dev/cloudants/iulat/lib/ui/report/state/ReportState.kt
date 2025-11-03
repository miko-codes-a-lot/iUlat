package dev.cloudants.iulat.lib.ui.report.state

data class ReportState(
    val isDialogVisible: Boolean = false,
    val reportText: String = "",
    val imageUri: String? = null
)
