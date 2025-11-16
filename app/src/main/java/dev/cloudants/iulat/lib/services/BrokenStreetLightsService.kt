package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.BrokenStreetlightsDto

interface BrokenStreetLightsService {
    suspend fun create(brokenStreetlightsDto: BrokenStreetlightsDto): BrokenStreetlightsDto
    suspend fun getAll(userId: String? = null): List<BrokenStreetlightsDto>
    suspend fun getById(id: String): BrokenStreetlightsDto?
    suspend fun update(id: String, garbage: BrokenStreetlightsDto): BrokenStreetlightsDto
    suspend fun delete(id: String)
}