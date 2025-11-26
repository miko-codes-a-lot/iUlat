package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.GarbageDisposalDto
import dev.cloudants.iulat.lib.models.entities.UserDto

interface GarbageDisposalService {
    suspend fun create(garbage: GarbageDisposalDto): GarbageDisposalDto
    suspend fun getAll(userId: String? = null): List<GarbageDisposalDto>
    suspend fun getById(id: String): GarbageDisposalDto?
    suspend fun update(id: String, garbage: GarbageDisposalDto): GarbageDisposalDto
    suspend fun delete(id: String)
}