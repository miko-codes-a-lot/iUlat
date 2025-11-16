package dev.cloudants.iulat.lib.state

import dev.cloudants.iulat.lib.models.entities.OthersDto

data class OthersState(
    val items: List<OthersDto> = emptyList(),
    val isLoading: Boolean = false,
    val isDialogVisible: Boolean = false,
    val error: String? = null,
    val selectedReport: OthersDto? = null
)