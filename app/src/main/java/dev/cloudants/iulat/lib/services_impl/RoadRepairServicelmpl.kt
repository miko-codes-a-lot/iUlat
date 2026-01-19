package dev.cloudants.iulat.lib.services_impl

import android.util.Log
import com.couchbase.lite.Collection
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import dev.cloudants.iulat.lib.models.entities.NotifyDto
import dev.cloudants.iulat.lib.models.entities.RoadRepairDto
import dev.cloudants.iulat.lib.services.NotificationService
import dev.cloudants.iulat.lib.services.RoadRepairService
import dev.cloudants.iulat.shared.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class RoadRepairServicelmpl @Inject constructor(
    private val db: Database,
    private val notificationService: NotificationService,
    private val sessionManager: SessionManager,
) : RoadRepairService {
    private val collection: Collection by lazy {
        db.getCollection("road_repair")
            ?: throw IllegalStateException("Collection 'road_repair' not found.")
    }
    private val ADMIN_ID = "SYSTEM_ADMIN_001"
    override suspend fun create(roadRepairDto: RoadRepairDto): RoadRepairDto {
        return try {
            val targetAdminId = sessionManager.adminIdFlow.firstOrNull() ?: ADMIN_ID
            val id = roadRepairDto.id ?: UUID.randomUUID().toString()
            val now = Date().toString()
            val status = roadRepairDto.status.takeIf { it.isNotBlank() } ?: "Pending"

            val roadToSave = roadRepairDto.copy(
                id = id,
                createdAt = now,
                lastUpdatedAt = now,
                status = status
            )

            val doc = MutableDocument(id).apply {
                setString("id", roadToSave.id)
                setString("userId", roadToSave.userId)
                val lat = roadToSave.latitude ?: 0.0
                val lng = roadToSave.longitude ?: 0.0
                setString("addressId", roadToSave.addressId)
                setDouble("latitude", lat)
                setDouble("longitude", lng)
                setString("reportDetails", roadToSave.reportDetails)
                setString("reportImage", roadToSave.reportImage)
                setString("status", roadToSave.status)
                setString("createdAt", roadToSave.createdAt)
                setString("lastUpdatedAt", roadToSave.lastUpdatedAt)
                setString("createdById", roadToSave.createdById)
                setString("reviewById", roadToSave.reviewById)
                setString("lastUpdatedById", roadToSave.lastUpdatedById)
                setString("deletedById", roadToSave.deletedById)
                setString("deletedAt", roadToSave.deletedAt)
            }

            collection.save(doc)
            val details = roadToSave.reportDetails
            val previewText = if (details.length > 15) {
                "${details.take(15)}..."
            } else {
                details
            }
            val adminNotification = NotifyDto(
                sender = roadToSave.userId,
                receiver = targetAdminId,
                documentId = roadToSave.id,
                documentType = "RoadRepair",
                message = "New Road Repair Report: $previewText",
                createdAt = Clock.System.now()
            )
            notificationService.sendNotification(adminNotification)
            Log.d("RoadRepairServiceImpl", "Created road repair report: $roadToSave")
            roadToSave
        } catch (e: Exception) {
            Log.e("RoadRepairServiceImpl", "Failed to create road repair report: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getAll(userId: String?): List<RoadRepairDto> {
        return try {
            val query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(collection))

            val results = query.execute().allResults()
            results.mapNotNull { result ->
                val dict = result.getDictionary(collection.name)
                dict?.let {
                    try {
                        val dto = Json { ignoreUnknownKeys = true }.decodeFromString<RoadRepairDto>(it.toJSON())
                        if (userId == null || dto.userId == userId) dto else null
                    } catch (e: Exception) {
                        Log.e("RoadRepairServiceImpl", "Error decoding others: ${e.message}")
                        null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("RoadRepairServiceImpl", "Failed to fetch road repair reports: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getById(id: String): RoadRepairDto? {
        return try {
            val doc = collection.getDocument(id) ?: return null
            Json { ignoreUnknownKeys = true }.decodeFromString<RoadRepairDto>(doc.toJSON())
        } catch (e: Exception) {
            Log.e("RoadRepairServiceImpl", "Failed to fetch road repair by id $id: ${e.message}")
            null
        }
    }

    override suspend fun update(id: String, roadRepairDto: RoadRepairDto): RoadRepairDto {
        return try {
            val doc = collection.getDocument(id)?.toMutable() ?: MutableDocument(id)

            val updated = roadRepairDto.copy(lastUpdatedAt = Date().toString())
            val jsonString = Json.encodeToString(RoadRepairDto.serializer(), updated)

            doc.setJSON(jsonString)
            collection.save(doc)
            updated
        } catch (e: Exception) {
            Log.e("RoadRepairServiceImpl", "Failed to update road repair serviceImpl: ${e.message}", e)
            throw e
        }
    }

    override suspend fun delete(id: String) {
        try {
            val doc = collection.getDocument(id) ?: return
            collection.delete(doc)
            Log.d("RoadRepairServiceImpl", "Deleted road repair report with id: $id")
        } catch (e: Exception) {
            Log.e("RoadRepairServiceImpl", "Failed to delete road repair report: ${e.message}", e)
        }
    }
}