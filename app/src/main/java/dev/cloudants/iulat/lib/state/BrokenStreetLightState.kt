package dev.cloudants.iulat.lib.state

import dev.cloudants.iulat.lib.models.entities.BrokenStreetlightsDto

data class BrokenStreetLightState(
    val items: List<BrokenStreetlightsDto> = emptyList(),
    val isLoading: Boolean = false,
    val isDialogVisible: Boolean = false,
    val error: String? = null,
    val selectedReport: BrokenStreetlightsDto? = null
)