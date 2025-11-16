package dev.cloudants.iulat.lib.services_impl

import android.util.Log
import com.couchbase.lite.Collection
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import dev.cloudants.iulat.lib.models.entities.RoadRepairDto
import dev.cloudants.iulat.lib.models.entities.RobberiesDto
import dev.cloudants.iulat.lib.services.RobberiesService
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class RobberiesServicelmpl @Inject constructor(
    private val db: Database
) : RobberiesService {
    private val collection: Collection by lazy {
        db.getCollection("robberies")
            ?: throw IllegalStateException("Collection 'robberies' not found.")
    }
    override suspend fun create(robberiesDto: RobberiesDto): RobberiesDto {
        return try {
            val id = robberiesDto.id ?: UUID.randomUUID().toString()
            val now = Date().toString()
            val others = robberiesDto.copy(id = id,createdAt = now,lastUpdatedAt = now)
            val jsonString = Json.encodeToString(RobberiesDto.serializer(), others)
            val doc = MutableDocument(id).apply { setJSON(jsonString) }
            collection.save(doc)
            Log.d("RobberiesServiceImpl", "Created robberies report: $others")
            others
        } catch (e: Exception) {
            Log.e("RobberiesServiceImpl", "Failed to create robberies report: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getAll(userId: String?): List<RobberiesDto> {
        return try {
            val query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(collection))

            val results = query.execute().allResults()
            results.mapNotNull { result ->
                val dict = result.getDictionary(collection.name)
                dict?.let {
                    try {
                        val dto = Json { ignoreUnknownKeys = true }.decodeFromString<RobberiesDto>(it.toJSON())
                        if (userId == null || dto.userId == userId) dto else null
                    } catch (e: Exception) {
                        Log.e("RobberiesServiceImpl", "Error decoding others: ${e.message}")
                        null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("RobberiesServiceImpl", "Failed to fetch robberies reports: ${e.message}")
            emptyList()
        }
    }


    override suspend fun getById(id: String): RobberiesDto? {
        return try {
            val doc = collection.getDocument(id) ?: return null
            Json { ignoreUnknownKeys = true }.decodeFromString<RobberiesDto>(doc.toJSON())
        } catch (e: Exception) {
            Log.e("RobberiesServiceImpl", "Failed to fetch robberies by id $id: ${e.message}")
            null
        }
    }

    override suspend fun update(id: String, robberiesDto: RobberiesDto): RobberiesDto {
        return try {
            val doc = collection.getDocument(id)?.toMutable() ?: MutableDocument(id)

            val updated = robberiesDto.copy(lastUpdatedAt = Date().toString())
            val jsonString = Json.encodeToString(RobberiesDto.serializer(), updated)

            doc.setJSON(jsonString)
            collection.save(doc)
            updated
        } catch (e: Exception) {
            Log.e("RobberiesServiceImpl", "Failed to update robberies serviceImpl: ${e.message}", e)
            throw e
        }
    }

    override suspend fun delete(id: String) {
        try {
            val doc = collection.getDocument(id) ?: return
            collection.delete(doc)
            Log.d("RobberiesServiceImpl", "Deleted robberies report with id: $id")
        } catch (e: Exception) {
            Log.e("RobberiesServiceImpl", "Failed to delete robberies report: ${e.message}", e)
        }
    }
}