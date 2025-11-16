package dev.cloudants.iulat.lib.state

import dev.cloudants.iulat.lib.models.entities.PublicDisturbanceDto

data class PublicDisturbanceState(
    val items: List<PublicDisturbanceDto> = emptyList(),
    val isLoading: Boolean = false,
    val isDialogVisible: Boolean = false,
    val error: String? = null,
    val selectedReport: PublicDisturbanceDto? = null
)