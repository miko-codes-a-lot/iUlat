package dev.cloudants.iulat.lib.ui.report.intent

sealed class ReportIntent {
    data class SubmitReport(val reportContent: String) : ReportIntent()
    object DismissDialog : ReportIntent()
}