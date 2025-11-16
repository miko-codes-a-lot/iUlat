package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.RobberiesDto

interface RobberiesService {
    suspend fun create(robberiesDto: RobberiesDto): RobberiesDto
    suspend fun getAll(userId: String? = null): List<RobberiesDto>
    suspend fun getById(id: String): RobberiesDto?
    suspend fun update(id: String, robberiesDto: RobberiesDto): RobberiesDto
    suspend fun delete(id: String)
}