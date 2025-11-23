package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.AddressDto
import dev.cloudants.iulat.lib.models.entities.UserDto

interface UserService {
    fun findOne(id: String): UserDto
    fun findAll(): List<UserDto>
    fun create(user: UserDto): UserDto
    fun createAdminUser(user: UserDto): UserDto
    fun update(id: String, user: UserDto): UserDto
    fun delete(id: String): Boolean
    fun fetchOne(id: String): UserDto?
    fun findByEmail(email: String): UserDto?
    fun login(email: String, password: String): UserDto?
    fun saveZonesToDatabase(zones: List<AddressDto>): Boolean
    fun saveValidId(userId: String, imageUri: ByteArray?): Result<UserDto>
    fun isZoneExisting(province: String, municipality: String, barangay: String, zone: String): Boolean
}