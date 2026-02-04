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
import dev.cloudants.iulat.lib.models.entities.RoadRepairDto
import dev.cloudants.iulat.lib.models.entities.RobberiesDto
import dev.cloudants.iulat.lib.services.NotificationService
import dev.cloudants.iulat.lib.services.RobberiesService
import dev.cloudants.iulat.shared.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class RobberiesServicelmpl @Inject constructor(
    private val db: Database,
    private val notificationService: NotificationService,
    private val sessionManager: SessionManager,
) : RobberiesService {
    private val collection: Collection by lazy {
        db.getCollection("robberies")
            ?: throw IllegalStateException("Collection 'robberies' not found.")
    }
    private val ADMIN_ID = "SYSTEM_ADMIN_001"
    override suspend fun create(robberiesDto: RobberiesDto): RobberiesDto {
        return try {
            val targetAdminId = sessionManager.adminIdFlow.firstOrNull() ?: ADMIN_ID
            val id = robberiesDto.id ?: UUID.randomUUID().toString()
            val now = Date().toString()
            val status = robberiesDto.status.takeIf { it.isNotBlank() } ?: "Pending"

            val robberyToSave = robberiesDto.copy(
                id = id,
                createdAt = now,
                lastUpdatedAt = now,
                status = status
            )

            val doc = MutableDocument(id).apply {
                setString("id", robberyToSave.id)
                setString("userId", robberyToSave.userId)
                setString("reportDetails", robberyToSave.reportDetails)
                setString("reportImage", robberyToSave.reportImage)
                setString("reportVideo", robberyToSave.reportVideo)
                setString("status", robberyToSave.status)
                setString("createdAt", robberyToSave.createdAt)
                setString("lastUpdatedAt", robberyToSave.lastUpdatedAt)
                setString("createdById", robberyToSave.createdById)
                setString("reviewById", robberyToSave.reviewById)
                setString("lastUpdatedById", robberyToSave.lastUpdatedById)
                setString("deletedById", robberyToSave.deletedById)
                setString("deletedAt", robberyToSave.deletedAt)
            }

            collection.save(doc)
            val details = robberyToSave.reportDetails
            val previewText = if (details.length > 15) {
                "${details.take(15)}..."
            } else {
                details
            }
            val adminNotification = NotifyDto(
                sender = robberyToSave.userId,
                receiver = targetAdminId,
                documentId = robberyToSave.id,
                documentType = "Robberies",
                message = "New Robberies Report: $previewText",
                createdAt = Clock.System.now()
            )
            notificationService.sendNotification(adminNotification)
            Log.d("RobberiesServiceImpl", "Created robberies report: $robberyToSave")
            robberyToSave
        } catch (e: Exception) {
            Log.e("RobberiesServiceImpl", "Failed to create robberies report: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getAll(userId: String?): List<RobberiesDto> {
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
                    RobberiesDto(
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