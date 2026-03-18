package dev.cloudants.iulat.lib.services_impl

import android.util.Log
import com.couchbase.lite.Collection
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Expression
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Query
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import dev.cloudants.iulat.lib.components.context.parseToSortableDate
import dev.cloudants.iulat.lib.models.entities.GarbageDisposalDto
import dev.cloudants.iulat.lib.models.entities.NotifyDto
import dev.cloudants.iulat.lib.models.entities.PublicDisturbanceDto
import dev.cloudants.iulat.lib.services.NotificationService
import dev.cloudants.iulat.lib.services.PublicDisturbanceService
import dev.cloudants.iulat.shared.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import java.util.Date
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

class PublicDisturbanceServicelmpl @Inject constructor(
    private val db: Database,
    private val notificationService: NotificationService,
    private val sessionManager: SessionManager,
) : PublicDisturbanceService {
    private val collection: Collection by lazy {
        db.getCollection("public_disturbance")
            ?: throw IllegalStateException("Collection 'public_disturbance' not found.")
    }
    private val ADMIN_ID = "SYSTEM_ADMIN_001"
    override suspend fun create(publicDisturbanceDto: PublicDisturbanceDto): PublicDisturbanceDto {
        return try {
            val targetAdminId = sessionManager.adminIdFlow.firstOrNull() ?: ADMIN_ID
            val id = publicDisturbanceDto.id ?: UUID.randomUUID().toString()
            val now = Date().toString()
            val finalCreatedAt = if (!publicDisturbanceDto.createdAt.isNullOrBlank()) publicDisturbanceDto.createdAt else now

            val status = publicDisturbanceDto.status.takeIf { it.isNotBlank() } ?: "Pending"

            val publicToSave = publicDisturbanceDto.copy(
                id = id,
                createdAt = finalCreatedAt,
                lastUpdatedAt = now,
                status = status
            )

            val doc = MutableDocument(id).apply {
                setString("id", publicToSave.id)
                val lat = publicToSave.latitude ?: 0.0
                val lng = publicToSave.longitude ?: 0.0
                setString("addressId", publicToSave.addressId)
                setDouble("latitude", lat)
                setDouble("longitude", lng)
                setString("userId", publicToSave.userId)
                setString("reportDetails", publicToSave.reportDetails)
                setString("reportImage", publicToSave.reportImage)
                setString("reportVideo", publicToSave.reportVideo)
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
            val details = publicToSave.reportDetails
            val previewText = if (details.length > 15) {
                "${details.take(15)}..."
            } else {
                details
            }
            val adminNotification = NotifyDto(
                sender = publicToSave.userId,
                receiver = targetAdminId,
                documentId = publicToSave.id,
                documentType = "PublicDisturbance",
                message = "New Public Disturbance Report: $previewText",
                createdAt = Clock.System.now()
            )
            notificationService.sendNotification(adminNotification)
            Log.d("PublicDisturbanceServiceImpl", "Created public disturbance report: $publicToSave")
            publicToSave
        } catch (e: Exception) {
            Log.e("PublicDisturbanceServiceImpl", "Failed to create public disturbance report: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getAll(userId: String?): List<PublicDisturbanceDto> {
        return try {
            val query: Query = if (!userId.isNullOrEmpty()) {
                QueryBuilder
                    .select(SelectResult.all())
                    .from(DataSource.collection(collection))
                    .where(Expression.property("userId").equalTo(Expression.string(userId)))
            } else {
                QueryBuilder
                    .select(SelectResult.all())
                    .from(DataSource.collection(collection))
            }
            val results = query.execute().allResults()
            results.mapNotNull { result ->
                val dict = result.getDictionary(collection.name)
                dict?.let {
                    PublicDisturbanceDto(
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
            }.sortedByDescending { dto -> parseToSortableDate(dto.createdAt) }
        } catch (e: Exception) {
            Log.e("PublicDisturbanceServiceImpl", "Failed to fetch reports: ${e.message}")
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
            val userNotification = NotifyDto(
                sender = ADMIN_ID,
                receiver = updated.userId,
                documentId = updated.id,
                documentType = "PublicDisturbance",
                message = "Your report status has been updated to: ${updated.status}",
                createdAt = Clock.System.now()
            )
            notificationService.sendNotification(userNotification)
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