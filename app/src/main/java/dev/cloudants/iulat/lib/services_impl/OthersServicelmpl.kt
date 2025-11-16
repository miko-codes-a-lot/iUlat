package dev.cloudants.iulat.lib.services_impl

import android.util.Log
import com.couchbase.lite.Collection
import com.couchbase.lite.DataSource
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import dev.cloudants.iulat.lib.models.entities.OthersDto
import dev.cloudants.iulat.lib.services.OthersService
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class OthersServicelmpl @Inject constructor(
    private val collection: Collection
) : OthersService {

    override suspend fun create(othersDto: OthersDto): OthersDto {
        return try {
            val id = othersDto.id ?: UUID.randomUUID().toString()
            val now = Date().toString()
            val others = othersDto.copy(id = id,createdAt = now,lastUpdatedAt = now)
            val jsonString = Json.encodeToString(OthersDto.serializer(), others)
            val doc = MutableDocument(id).apply { setJSON(jsonString) }
            collection.save(doc)
            Log.d("OthersServiceImpl", "Created others report: $others")
            others
        } catch (e: Exception) {
            Log.e("OthersServiceImpl", "Failed to create others report: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getAll(userId: String?): List<OthersDto> {
        return try {
            val query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(collection))

            val results = query.execute().allResults()
            results.mapNotNull { result ->
                val dict = result.getDictionary(collection.name)
                dict?.let {
                    try {
                        val dto = Json { ignoreUnknownKeys = true }.decodeFromString<OthersDto>(it.toJSON())
                        if (userId == null || dto.userId == userId) dto else null
                    } catch (e: Exception) {
                        Log.e("OthersServiceImpl", "Error decoding others: ${e.message}")
                        null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("OthersServiceImpl", "Failed to fetch others reports: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getById(id: String): OthersDto? {
        return try {
            val doc = collection.getDocument(id) ?: return null
            Json { ignoreUnknownKeys = true }.decodeFromString<OthersDto>(doc.toJSON())
        } catch (e: Exception) {
            Log.e("OthersServiceImpl", "Failed to fetch others by id $id: ${e.message}")
            null
        }
    }

    override suspend fun update(id: String, otherDto: OthersDto): OthersDto {
        return try {
            val doc = collection.getDocument(id)?.toMutable() ?: MutableDocument(id)

            val updated = otherDto.copy(lastUpdatedAt = Date().toString())
            val jsonString = Json.encodeToString(OthersDto.serializer(), updated)

            doc.setJSON(jsonString)
            collection.save(doc)
            updated
        } catch (e: Exception) {
            Log.e("OthersServiceImpl", "Failed to update others: ${e.message}", e)
            throw e
        }
    }

    override suspend fun delete(id: String) {
        try {
            val doc = collection.getDocument(id) ?: return
            collection.delete(doc)
            Log.d("OthersServiceImpl", "Deleted others report with id: $id")
        } catch (e: Exception) {
            Log.e("OthersServiceImpl", "Failed to delete others report: ${e.message}", e)
        }
    }
}