package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.PublicDisturbanceDto

interface PublicDisturbanceService {
    suspend fun create(publicDisturbanceDto: PublicDisturbanceDto): PublicDisturbanceDto
    suspend fun getAll(userId: String? = null): List<PublicDisturbanceDto>
    suspend fun getById(id: String): PublicDisturbanceDto?
    suspend fun update(id: String, publicDisturbanceDto: PublicDisturbanceDto): PublicDisturbanceDto
    suspend fun delete(id: String)
}