package dev.cloudants.iulat.lib.state

import dev.cloudants.iulat.lib.models.entities.NoWaterSupplyDto

data class NoWaterSupplyState(
    val items: List<NoWaterSupplyDto> = emptyList(),
    val isLoading: Boolean = false,
    val isDialogVisible: Boolean = false,
    val error: String? = null,
    val selectedReport: NoWaterSupplyDto? = null
)