package dev.cloudants.iulat.lib.state

import dev.cloudants.iulat.lib.models.entities.GarbageDisposalDto

data class GarbageDisposalState(
    val items: List<GarbageDisposalDto> = emptyList(),
    val isLoading: Boolean = false,
    val isDialogVisible: Boolean = false,
    val error: String? = null
)