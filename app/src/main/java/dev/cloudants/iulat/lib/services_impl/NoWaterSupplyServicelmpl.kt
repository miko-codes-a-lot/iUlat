package dev.cloudants.iulat.lib.services_impl

import android.util.Log
import com.couchbase.lite.Collection
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import dev.cloudants.iulat.lib.components.context.parseToSortableDate
import dev.cloudants.iulat.lib.models.entities.GarbageDisposalDto
import dev.cloudants.iulat.lib.models.entities.NoWaterSupplyDto
import dev.cloudants.iulat.lib.models.entities.NotifyDto
import dev.cloudants.iulat.lib.services.NoWaterSupplyService
import dev.cloudants.iulat.lib.services.NotificationService
import dev.cloudants.iulat.shared.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class NoWaterSupplyServicelmpl @Inject constructor(
    private val db: Database,
    private val notificationService: NotificationService,
    private val sessionManager: SessionManager,
) : NoWaterSupplyService {
    private val collection: Collection by lazy {
        db.getCollection("no_water_supply")
            ?: throw IllegalStateException("Collection 'no_water_supply' not found.")
    }
    private val ADMIN_ID = "SYSTEM_ADMIN_001"
    override suspend fun create(noWaterSupplyDto: NoWaterSupplyDto): NoWaterSupplyDto {
        return try {
            val targetAdminId = sessionManager.adminIdFlow.firstOrNull() ?: ADMIN_ID
            val id = noWaterSupplyDto.id ?: UUID.randomUUID().toString()
            val now = Date().toString()
            val status = noWaterSupplyDto.status.takeIf { it.isNotBlank() } ?: "Pending"

            val noWaterToSave = noWaterSupplyDto.copy(
                id = id,
                createdAt = now,
                lastUpdatedAt = now,
                status = status
            )

            val doc = MutableDocument(id).apply {
                setString("id", noWaterToSave.id)
                setString("userId", noWaterToSave.userId)
                val lat = noWaterToSave.latitude ?: 0.0
                val lng = noWaterToSave.longitude ?: 0.0
                setString("addressId", noWaterToSave.addressId)
                setDouble("latitude", lat)
                setDouble("longitude", lng)
                setString("reportDetails", noWaterToSave.reportDetails)
                setString("reportImage", noWaterToSave.reportImage)
                setString("reportVideo", noWaterToSave.reportVideo)
                setString("status", noWaterToSave.status)
                setString("createdAt", noWaterToSave.createdAt)
                setString("lastUpdatedAt", noWaterToSave.lastUpdatedAt)
                setString("createdById", noWaterToSave.createdById)
                setString("reviewById", noWaterToSave.reviewById)
                setString("lastUpdatedById", noWaterToSave.lastUpdatedById)
                setString("deletedById", noWaterToSave.deletedById)
                setString("deletedAt", noWaterToSave.deletedAt)
            }

            collection.save(doc)
            val details = noWaterToSave.reportDetails
            val previewText = if (details.length > 15) {
                "${details.take(15)}..."
            } else {
                details
            }
            val adminNotification = NotifyDto(
                sender = noWaterToSave.userId,
                receiver = targetAdminId,
                documentId = noWaterToSave.id,
                documentType = "NoWaterSupply",
                message = "New No Water Supply Report: $previewText",
                createdAt = Clock.System.now()
            )
            notificationService.sendNotification(adminNotification)
            Log.d("NoWaterSupplyServiceImpl", "Created no water supply report: $noWaterToSave")
            noWaterToSave
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
                    NoWaterSupplyDto(
                        id = it.getString("id"),
                        userId = it.getString("userId") ?: "",
                        status = it.getString("status") ?: "Pending",
                        createdAt = it.getString("createdAt"),
                        reportDetails = it.getString("reportDetails") ?: "",
                        reportImage = null,
                        reportVideo = null
                    )
                }
            }
            .sortedByDescending { dto -> parseToSortableDate(dto.createdAt) }
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