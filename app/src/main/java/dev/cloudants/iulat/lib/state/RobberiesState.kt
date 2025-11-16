package dev.cloudants.iulat.lib.state

import dev.cloudants.iulat.lib.models.entities.RobberiesDto

data class RobberiesState(
    val items: List<RobberiesDto> = emptyList(),
    val isLoading: Boolean = false,
    val isDialogVisible: Boolean = false,
    val error: String? = null,
    val selectedReport: RobberiesDto? = null
)