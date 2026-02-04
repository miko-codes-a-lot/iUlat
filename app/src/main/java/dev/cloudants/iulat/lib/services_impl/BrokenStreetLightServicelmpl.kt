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
import dev.cloudants.iulat.lib.models.entities.BrokenStreetlightsDto
import dev.cloudants.iulat.lib.models.entities.GarbageDisposalDto
import dev.cloudants.iulat.lib.models.entities.NotifyDto
import dev.cloudants.iulat.lib.services.BrokenStreetLightsService
import dev.cloudants.iulat.lib.services.NotificationService
import dev.cloudants.iulat.shared.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class BrokenStreetLightServicelmpl @Inject constructor(
    private val db: Database,
    private val notificationService: NotificationService,
    private val sessionManager: SessionManager,
) : BrokenStreetLightsService {
    private val collection: Collection by lazy {
        db.getCollection("broken_streetlights")
            ?: throw IllegalStateException("Collection 'broken_streetlights' not found.")
    }
    private val ADMIN_ID = "SYSTEM_ADMIN_001"
    override suspend fun create(brokenStreetlightsDto: BrokenStreetlightsDto): BrokenStreetlightsDto {
        return try {
            val targetAdminId = sessionManager.adminIdFlow.firstOrNull() ?: ADMIN_ID
            val id = brokenStreetlightsDto.id ?: UUID.randomUUID().toString()
            val now = Date().toString()
            val status = brokenStreetlightsDto.status.takeIf { it.isNotBlank() } ?: "Pending"
            val brokenStreetToSave = brokenStreetlightsDto.copy(
                id = id,
                createdAt = now,
                lastUpdatedAt = now,
                status = status
            )

            val doc = MutableDocument(id).apply {
                setString("id", brokenStreetToSave.id)
                setString("userId", brokenStreetToSave.userId)
                val lat = brokenStreetToSave.latitude ?: 0.0
                val lng = brokenStreetToSave.longitude ?: 0.0
                setString("addressId", brokenStreetToSave.addressId)
                setDouble("latitude", lat)
                setDouble("longitude", lng)
                setString("reportDetails", brokenStreetToSave.reportDetails)
                setString("reportImage", brokenStreetToSave.reportImage)
                setString("reportVideo", brokenStreetToSave.reportVideo)
                setString("status", brokenStreetToSave.status)
                setString("createdAt", brokenStreetToSave.createdAt)
                setString("lastUpdatedAt", brokenStreetToSave.lastUpdatedAt)
                setString("createdById", brokenStreetToSave.createdById)
                setString("reviewById", brokenStreetToSave.reviewById)
                setString("lastUpdatedById", brokenStreetToSave.lastUpdatedById)
                setString("deletedById", brokenStreetToSave.deletedById)
                setString("deletedAt", brokenStreetToSave.deletedAt)
            }

            collection.save(doc)
            val details = brokenStreetToSave.reportDetails
            val previewText = if (details.length > 15) {
                "${details.take(15)}..."
            } else {
                details
            }
            val adminNotification = NotifyDto(
                sender = brokenStreetToSave.userId,
                receiver = targetAdminId,
                documentId = brokenStreetToSave.id,
                documentType = "BrokenStreetlights",
                message = "New Broken Streetlights Report: $previewText",
                createdAt = Clock.System.now()
            )
            notificationService.sendNotification(adminNotification)
            Log.d("BrokenStreetLightsServiceImpl", "Created broken street light report: $brokenStreetToSave")
            brokenStreetToSave
        } catch (e: Exception) {
            Log.e("BrokenStreetLightsServiceImpl", "Failed to create broken street light report: ${e.message}", e)
            throw e
        }
    }


    override suspend fun getAll(userId: String?): List<BrokenStreetlightsDto> {
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
                    BrokenStreetlightsDto(
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