package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.RoadRepairDto

interface RoadRepairService {
    suspend fun create(roadRepairDto: RoadRepairDto): RoadRepairDto
    suspend fun getAll(userId: String? = null): List<RoadRepairDto>
    suspend fun getById(id: String): RoadRepairDto?
    suspend fun update(id: String, roadRepairDto: RoadRepairDto): RoadRepairDto
    suspend fun delete(id: String)
}