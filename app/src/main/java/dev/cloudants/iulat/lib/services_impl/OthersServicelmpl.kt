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
import dev.cloudants.iulat.lib.models.entities.NotifyDto
import dev.cloudants.iulat.lib.models.entities.OthersDto
import dev.cloudants.iulat.lib.services.NotificationService
import dev.cloudants.iulat.lib.services.OthersService
import dev.cloudants.iulat.shared.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class OthersServicelmpl @Inject constructor(
    private val db: Database,
    private val notificationService: NotificationService,
    private val sessionManager: SessionManager,
) : OthersService {
    private val collection: Collection by lazy {
        db.getCollection("others")
            ?: throw IllegalStateException("Collection 'others' not found.")
    }
    private val ADMIN_ID = "SYSTEM_ADMIN_001"
    override suspend fun create(othersDto: OthersDto): OthersDto {
        return try {
            val targetAdminId = sessionManager.adminIdFlow.firstOrNull() ?: ADMIN_ID
            val id = othersDto.id ?: UUID.randomUUID().toString()
            val now = Date().toString()
            val status = othersDto.status.takeIf { it.isNotBlank() } ?: "Pending"

            val othersToSave = othersDto.copy(
                id = id,
                createdAt = now,
                lastUpdatedAt = now,
                status = status
            )

            val doc = MutableDocument(id).apply {
                setString("id", othersToSave.id)
                setString("userId", othersToSave.userId)
                val lat = othersToSave.latitude ?: 0.0
                val lng = othersToSave.longitude ?: 0.0
                setString("addressId", othersToSave.addressId)
                setDouble("latitude", lat)
                setDouble("longitude", lng)
                setString("reportDetails", othersToSave.reportDetails)
                setString("reportImage", othersToSave.reportImage)
                setString("reportVideo", othersToSave.reportVideo)
                setString("status", othersToSave.status)
                setString("createdAt", othersToSave.createdAt)
                setString("lastUpdatedAt", othersToSave.lastUpdatedAt)
                setString("createdById", othersToSave.createdById)
                setString("reviewById", othersToSave.reviewById)
                setString("lastUpdatedById", othersToSave.lastUpdatedById)
                setString("deletedById", othersToSave.deletedById)
                setString("deletedAt", othersToSave.deletedAt)
            }

            collection.save(doc)
            val details = othersToSave.reportDetails
            val previewText = if (details.length > 15) {
                "${details.take(15)}..."
            } else {
                details
            }
            val adminNotification = NotifyDto(
                sender = othersToSave.userId,
                receiver = targetAdminId,
                documentId = othersToSave.id,
                documentType = "Others",
                message = "New Others Report: $previewText",
                createdAt = Clock.System.now()
            )
            notificationService.sendNotification(adminNotification)
            Log.d("OthersServiceImpl", "Created others report: $othersToSave")
            othersToSave
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
                    OthersDto(
                        id = it.getString("id"),
                        userId = it.getString("userId") ?: "",
                        addressId = it.getString("addressId"),
                        latitude = it.getDouble("latitude"),
                        longitude = it.getDouble("longitude"),
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