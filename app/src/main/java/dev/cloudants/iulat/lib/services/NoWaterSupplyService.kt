package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.NoWaterSupplyDto

interface NoWaterSupplyService {
    suspend fun create(noWaterSupplyDto: NoWaterSupplyDto): NoWaterSupplyDto
    suspend fun getAll(userId: String? = null): List<NoWaterSupplyDto>
    suspend fun getById(id: String): NoWaterSupplyDto?
    suspend fun update(id: String, garbage: NoWaterSupplyDto): NoWaterSupplyDto
    suspend fun delete(id: String)
}