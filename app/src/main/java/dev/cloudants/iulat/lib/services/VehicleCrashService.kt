package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.VehicleCrashDto

interface VehicleCrashService {
    suspend fun create(vehicleCrashDto: VehicleCrashDto): VehicleCrashDto
    suspend fun getAll(userId: String? = null): List<VehicleCrashDto>
    suspend fun getById(id: String): VehicleCrashDto?
    suspend fun update(id: String, vehicleCrashDto: VehicleCrashDto): VehicleCrashDto
    suspend fun delete(id: String)
}