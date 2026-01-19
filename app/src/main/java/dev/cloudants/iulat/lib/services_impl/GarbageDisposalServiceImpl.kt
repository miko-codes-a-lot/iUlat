package dev.cloudants.iulat.lib.services_impl

import android.util.Log
import com.couchbase.lite.*
import dev.cloudants.iulat.lib.models.entities.GarbageDisposalDto
import dev.cloudants.iulat.lib.services.GarbageDisposalService
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.couchbase.lite.DataSource
import com.couchbase.lite.Collection
import dev.cloudants.iulat.lib.components.context.parseToSortableDate
import dev.cloudants.iulat.lib.models.entities.NotifyDto
import dev.cloudants.iulat.lib.services.NotificationService
import dev.cloudants.iulat.shared.SessionManager
import jakarta.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import java.util.Date
import java.util.UUID
import kotlinx.serialization.json.Json

class GarbageDisposalServiceImpl @Inject constructor(
    private val db: Database,
    private val notificationService: NotificationService,
    private val sessionManager: SessionManager,
) : GarbageDisposalService {
    private val collection: Collection by lazy {
        db.getCollection("garbage_disposal")
            ?: throw IllegalStateException("Collection 'garbage_disposal' not found.")
    }
    private val ADMIN_ID = "SYSTEM_ADMIN_001"
    override suspend fun create(garbage: GarbageDisposalDto): GarbageDisposalDto {
        return try {
            val targetAdminId = sessionManager.adminIdFlow.firstOrNull() ?: ADMIN_ID
            val id = garbage.id ?: UUID.randomUUID().toString()
            val now = Date().toString()
            val status = garbage.status?.takeIf { it.isNotBlank() } ?: "Pending"
            val garbageToSave = garbage.copy(id = id, createdAt = now, lastUpdatedAt = now, status = status)

            val doc = MutableDocument(id).apply {
                setString("id", garbageToSave.id)
                val lat = garbageToSave.latitude ?: 0.0
                val lng = garbageToSave.longitude ?: 0.0
                setString("userId", garbageToSave.userId)
                setString("addressId", garbageToSave.addressId)
                setDouble("latitude", lat)
                setDouble("longitude", lng)
                setString("email", garbageToSave.email)
                setString("mobileNumber", garbageToSave.mobileNumber)
                setString("reportDetails", garbageToSave.reportDetails)
                setString("reportImage", garbageToSave.reportImage)
                setString("status", garbageToSave.status)
                setString("createdAt", garbageToSave.createdAt)
                setString("lastUpdatedAt", garbageToSave.lastUpdatedAt)
                setString("createdById", garbageToSave.createdById)
                setString("reviewById", garbageToSave.reviewById)
                setString("lastUpdatedById", garbageToSave.lastUpdatedById)
                setString("deletedById", garbageToSave.deletedById)
                setString("deletedAt", garbageToSave.deletedAt)
            }

            collection.save(doc)
            val details = garbageToSave.reportDetails
            val previewText = if (details.length > 15) {
                "${details.take(15)}..."
            } else {
                details
            }
            val adminNotification = NotifyDto(
                sender = garbageToSave.userId,
                receiver = targetAdminId,
                documentId = garbageToSave.id,
                documentType = "GarbageDisposal",
                message = "New Garbage Disposal Report: $previewText",
                createdAt = Clock.System.now()
            )
            notificationService.sendNotification(adminNotification)
            Log.d("GarbageServiceImpl", "Created garbage report: $garbageToSave")
            garbageToSave
        } catch (e: Exception) {
            Log.e("GarbageServiceImpl", "Failed to create garbage report: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getAll(userId: String?): List<GarbageDisposalDto> {
        return try {
            val query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(collection))

            val results = query.execute().allResults()

            results.mapNotNull { result ->
                val dict = result.getDictionary(collection.name)
                dict?.let {
                    try {
                        val dto = Json { ignoreUnknownKeys = true }.decodeFromString<GarbageDisposalDto>(it.toJSON())
                        if (userId == null || dto.userId == userId) dto else null
                    } catch (e: Exception) {
                        Log.e("GarbageServiceImpl", "Error decoding GarbageDisposalDto: ${e.message}")
                        null
                    }
                }
            }
            .sortedByDescending { dto -> parseToSortableDate(dto.createdAt) }
        } catch (e: Exception) {
            Log.e("GarbageServiceImpl", "Failed to fetch garbage reports: ${e.message}")
            emptyList()
        }
    }


    override suspend fun update(id: String, garbage: GarbageDisposalDto): GarbageDisposalDto {
        return try {
            val doc = collection.getDocument(id)?.toMutable() ?: MutableDocument(id)

            val updated = garbage.copy(lastUpdatedAt = Date().toString())
            val jsonString = Json.encodeToString(GarbageDisposalDto.serializer(), updated)

            doc.setJSON(jsonString)
            collection.save(doc)

            updated
        } catch (e: Exception) {
            Log.e("GarbageServiceImpl", "Failed to update garbage: ${e.message}", e)
            throw e
        }
    }

    override suspend fun delete(id: String) {
        try {
            val doc = collection.getDocument(id) ?: return
            collection.delete(doc)
            Log.d("GarbageServiceImpl", "Deleted garbage report with id: $id")
        } catch (e: Exception) {
            Log.e("GarbageServiceImpl", "Failed to delete garbage report: ${e.message}", e)
        }
    }

    override suspend fun getById(id: String): GarbageDisposalDto? {
        return try {
            val doc = collection.getDocument(id) ?: return null
            Json { ignoreUnknownKeys = true }.decodeFromString<GarbageDisposalDto>(doc.toJSON())
        } catch (e: Exception) {
            Log.e("GarbageServiceImpl", "Failed to fetch garbage by id $id: ${e.message}")
            null
        }
    }
}
