package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.OthersDto

interface OthersService {
    suspend fun create(othersDto: OthersDto): OthersDto
    suspend fun getAll(userId: String? = null): List<OthersDto>
    suspend fun getById(id: String): OthersDto?
    suspend fun update(id: String, garbage: OthersDto): OthersDto
    suspend fun delete(id: String)
}