package dev.cloudants.iulat.lib.services_impl

import android.util.Log
import com.couchbase.lite.Collection
import com.couchbase.lite.DataSource
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import dev.cloudants.iulat.lib.models.entities.BrokenStreetlightsDto
import dev.cloudants.iulat.lib.models.entities.NoWaterSupplyDto
import dev.cloudants.iulat.lib.services.NoWaterSupplyService
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class NoWaterSupplyServicelmpl @Inject constructor(
    private val collection: Collection
) : NoWaterSupplyService {

    override suspend fun create(noWaterSupplyDto: NoWaterSupplyDto): NoWaterSupplyDto {
        return try {
            val id = noWaterSupplyDto.id ?: UUID.randomUUID().toString()
            val now = Date().toString()
            val noWaterSupply = noWaterSupplyDto.copy(id = id,createdAt = now,lastUpdatedAt = now)
            val jsonString = Json.encodeToString(NoWaterSupplyDto.serializer(), noWaterSupply)
            val doc = MutableDocument(id).apply { setJSON(jsonString) }
            collection.save(doc)
            Log.d("NoWaterSupplyServiceImpl", "Created no water supply report: $noWaterSupply")
            noWaterSupply
        } catch (e: Exception) {
            Log.e("NoWaterSupplyServiceImpl", "Failed to create no water supply report: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getAll(userId: String?): List<NoWaterSupplyDto> {
        return try {
            val query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(collection))

            val results = query.execute().allResults()
            results.mapNotNull { result ->
                val dict = result.getDictionary(collection.name)
                dict?.let {
                    try {
                        val dto = Json { ignoreUnknownKeys = true }.decodeFromString<NoWaterSupplyDto>(it.toJSON())
                        if (userId == null || dto.userId == userId) dto else null
                    } catch (e: Exception) {
                        Log.e("NoWaterSupplyServiceImpl", "Error decoding NoWaterSupplyDto: ${e.message}")
                        null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("NoWaterSupplyServiceImpl", "Failed to fetch no water supply reports: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getById(id: String): NoWaterSupplyDto? {
        return try {
            val doc = collection.getDocument(id) ?: return null
            Json { ignoreUnknownKeys = true }.decodeFromString<NoWaterSupplyDto>(doc.toJSON())
        } catch (e: Exception) {
            Log.e("NoWaterSupplyServiceImpl", "Failed to fetch no water supply by id $id: ${e.message}")
            null
        }
    }

    override suspend fun update(id: String, noWaterSupply: NoWaterSupplyDto): NoWaterSupplyDto {
        return try {
            val doc = collection.getDocument(id)?.toMutable() ?: MutableDocument(id)

            val updated = noWaterSupply.copy(lastUpdatedAt = Date().toString())
            val jsonString = Json.encodeToString(NoWaterSupplyDto.serializer(), updated)

            doc.setJSON(jsonString)
            collection.save(doc)
            updated
        } catch (e: Exception) {
            Log.e("NoWaterSupplyServiceImpl", "Failed to update no water supply: ${e.message}", e)
            throw e
        }
    }

    override suspend fun delete(id: String) {
        try {
            val doc = collection.getDocument(id) ?: return
            collection.delete(doc)
            Log.d("NoWaterSupplyServiceImpl", "Deleted no water supply report with id: $id")
        } catch (e: Exception) {
            Log.e("NoWaterSupplyServiceImpl", "Failed to delete no water supply report: ${e.message}", e)
        }
    }
}