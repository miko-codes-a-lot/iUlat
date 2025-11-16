package dev.cloudants.iulat.lib.state

import dev.cloudants.iulat.lib.models.entities.VehicleCrashDto

data class VehicleCrashState(
    val items: List<VehicleCrashDto> = emptyList(),
    val isLoading: Boolean = false,
    val isDialogVisible: Boolean = false,
    val error: String? = null,
    val selectedReport: VehicleCrashDto? = null
)