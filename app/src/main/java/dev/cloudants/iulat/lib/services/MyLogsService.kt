package dev.cloudants.iulat.lib.services

import dev.cloudants.iulat.lib.models.entities.MyLogsDto

interface MyLogsService {
    fun fetchAll(): List<MyLogsDto>
    fun fetchOne(id: String): MyLogsDto?
    fun logs(): MyLogsDto
}