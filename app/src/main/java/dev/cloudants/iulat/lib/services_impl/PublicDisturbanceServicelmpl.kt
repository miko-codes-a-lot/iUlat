package dev.cloudants.iulat.lib.services_impl

import android.util.Log
import com.couchbase.lite.Collection
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import dev.cloudants.iulat.lib.models.entities.PublicDisturbanceDto
import dev.cloudants.iulat.lib.services.PublicDisturbanceService
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class PublicDisturbanceServicelmpl @Inject constructor(
    private val db: Database
) : PublicDisturbanceService {
    private val collection: Collection by lazy {
        db.getCollection("public_disturbance")
            ?: throw IllegalStateException("Collection 'public_disturbance' not found.")
    }

    override suspend fun create(publicDisturbanceDto: PublicDisturbanceDto): PublicDisturbanceDto {
        return try {
            val id = publicDisturbanceDto.id ?: UUID.randomUUID().toString()
            val now = Date().toString()
            val status = publicDisturbanceDto.status.takeIf { it.isNotBlank() } ?: "Pending"

            val publicToSave = publicDisturbanceDto.copy(
                id = id,
                createdAt = now,
                lastUpdatedAt = now,
                status = status
            )

            val doc = MutableDocument(id).apply {
                setString("id", publicToSave.id)
                setString("userId", publicToSave.userId)
                setString("reportDetails", publicToSave.reportDetails)
                setString("reportImage", publicToSave.reportImage)
                setString("status", publicToSave.status)
                setString("createdAt", publicToSave.createdAt)
                setString("lastUpdatedAt", publicToSave.lastUpdatedAt)
                setString("createdById", publicToSave.createdById)
                setString("reviewById", publicToSave.reviewById)
                setString("lastUpdatedById", publicToSave.lastUpdatedById)
                setString("deletedById", publicToSave.deletedById)
                setString("deletedAt", publicToSave.deletedAt)
            }

            collection.save(doc)
            Log.d("PublicDisturbanceServiceImpl", "Created public disturbance report: $publicToSave")
            publicToSave
        } catch (e: Exception) {
            Log.e("PublicDisturbanceServiceImpl", "Failed to create public disturbance report: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getAll(userId: String?): List<PublicDisturbanceDto> {
        return try {
            val query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(collection))

            val results = query.execute().allResults()
            results.mapNotNull { result ->
                val dict = result.getDictionary(collection.name)
                dict?.let {
                    try {
                        val dto = Json { ignoreUnknownKeys = true }.decodeFromString<PublicDisturbanceDto>(it.toJSON())
                        if (userId == null || dto.userId == userId) dto else null
                    } catch (e: Exception) {
                        Log.e("PublicDisturbanceServiceImpl", "Error decoding others: ${e.message}")
                        null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PublicDisturbanceServiceImpl", "Failed to fetch public disturbance reports: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getById(id: String): PublicDisturbanceDto? {
        return try {
            val doc = collection.getDocument(id) ?: return null
            Json { ignoreUnknownKeys = true }.decodeFromString<PublicDisturbanceDto>(doc.toJSON())
        } catch (e: Exception) {
            Log.e("PublicDisturbanceServiceImpl", "Failed to fetch public disturbance by id $id: ${e.message}")
            null
        }
    }


    override suspend fun update(id: String, publicDisturbanceDto: PublicDisturbanceDto): PublicDisturbanceDto {
        return try {
            val doc = collection.getDocument(id)?.toMutable() ?: MutableDocument(id)

            val updated = publicDisturbanceDto.copy(lastUpdatedAt = Date().toString())
            val jsonString = Json.encodeToString(PublicDisturbanceDto.serializer(), updated)

            doc.setJSON(jsonString)
            collection.save(doc)
            updated
        } catch (e: Exception) {
            Log.e("PublicDisturbanceServiceImpl", "Failed to update public disturbance: ${e.message}", e)
            throw e
        }
    }

    override suspend fun delete(id: String) {
        try {
            val doc = collection.getDocument(id) ?: return
            collection.delete(doc)
            Log.d("PublicDisturbanceServiceImpl", "Deleted public disturbance report with id: $id")
        } catch (e: Exception) {
            Log.e("PublicDisturbanceServiceImpl", "Failed to delete public disturbance report: ${e.message}", e)
        }
    }
}