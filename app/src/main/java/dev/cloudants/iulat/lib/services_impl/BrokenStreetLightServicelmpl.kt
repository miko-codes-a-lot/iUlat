package dev.cloudants.iulat.lib.services_impl

import android.util.Log
import com.couchbase.lite.Collection
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import dev.cloudants.iulat.lib.models.entities.BrokenStreetlightsDto
import dev.cloudants.iulat.lib.services.BrokenStreetLightsService
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class BrokenStreetLightServicelmpl @Inject constructor(
    private val db: Database
) : BrokenStreetLightsService {
    private val collection: Collection by lazy {
        db.getCollection("broken_streetlights")
            ?: throw IllegalStateException("Collection 'broken_streetlights' not found.")
    }

    override suspend fun create(brokenStreetlightsDto: BrokenStreetlightsDto): BrokenStreetlightsDto {
        return try {
            val id = brokenStreetlightsDto.id ?: UUID.randomUUID().toString()
            val now = Date().toString()
            val brokenStreetToSave = brokenStreetlightsDto.copy(id = id,createdAt = now,lastUpdatedAt = now)
            val jsonString = Json.encodeToString(BrokenStreetlightsDto.serializer(), brokenStreetToSave)
            val doc = MutableDocument(id).apply { setJSON(jsonString) }
            collection.save(doc)
            Log.d("BrokenStreetLightsServiceImpl", "Created broken street light report: $brokenStreetToSave")
            brokenStreetToSave
        } catch (e: Exception) {
            Log.e("BrokenStreetLightsServiceImpl", "Failed to create broken street light report: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getAll(userId: String?): List<BrokenStreetlightsDto> {
        return try {
            val query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(collection))

            val results = query.execute().allResults()
            results.mapNotNull { result ->
                val dict = result.getDictionary(collection.name)
                dict?.let {
                    try {
                        val dto = Json { ignoreUnknownKeys = true }.decodeFromString<BrokenStreetlightsDto>(it.toJSON())
                        if (userId == null || dto.userId == userId) dto else null
                    } catch (e: Exception) {
                        Log.e("BrokenStreetLightsServiceImpl", "Error decoding BrokenStreetlightsDto: ${e.message}")
                        null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("BrokenStreetLightsServiceImpl", "Failed to fetch broken light reports: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getById(id: String): BrokenStreetlightsDto? {
        return try {
            val doc = collection.getDocument(id) ?: return null
            Json { ignoreUnknownKeys = true }.decodeFromString<BrokenStreetlightsDto>(doc.toJSON())
        } catch (e: Exception) {
            Log.e("BrokenStreetLightsServiceImpl", "Failed to fetch broken light by id $id: ${e.message}")
            null
        }
    }

    override suspend fun update(id: String, brokenLight: BrokenStreetlightsDto): BrokenStreetlightsDto {
        return try {
            val doc = collection.getDocument(id)?.toMutable() ?: MutableDocument(id)

            val updated = brokenLight.copy(lastUpdatedAt = Date().toString())
            val jsonString = Json.encodeToString(BrokenStreetlightsDto.serializer(), updated)

            doc.setJSON(jsonString)
            collection.save(doc)
            updated
        } catch (e: Exception) {
            Log.e("BrokenStreetLightsServiceImpl", "Failed to update broken light: ${e.message}", e)
            throw e
        }
    }

    override suspend fun delete(id: String) {
        try {
            val doc = collection.getDocument(id) ?: return
            collection.delete(doc)
            Log.d("BrokenStreetLightsServiceImpl", "Deleted broken light report with id: $id")
        } catch (e: Exception) {
            Log.e("BrokenStreetLightsServiceImpl", "Failed to delete broken light report: ${e.message}", e)
        }
    }
}