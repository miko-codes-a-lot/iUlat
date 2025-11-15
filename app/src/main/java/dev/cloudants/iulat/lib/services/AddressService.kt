package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.AddressDto

interface AddressService {
    fun fetchAll(): List<AddressDto>
    fun fetchOne(id: String): AddressDto?
    fun update(id: String, address: AddressDto): AddressDto
}