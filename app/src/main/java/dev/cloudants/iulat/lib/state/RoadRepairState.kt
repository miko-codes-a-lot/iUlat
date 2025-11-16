package dev.cloudants.iulat.lib.state

import dev.cloudants.iulat.lib.models.entities.RoadRepairDto

data class RoadRepairState(
    val items: List<RoadRepairDto> = emptyList(),
    val isLoading: Boolean = false,
    val isDialogVisible: Boolean = false,
    val error: String? = null,
    val selectedReport: RoadRepairDto? = null
)