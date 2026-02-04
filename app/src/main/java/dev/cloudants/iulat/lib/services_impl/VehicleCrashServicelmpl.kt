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
import dev.cloudants.iulat.lib.models.entities.VehicleCrashDto
import dev.cloudants.iulat.lib.services.NotificationService
import dev.cloudants.iulat.lib.services.VehicleCrashService
import dev.cloudants.iulat.shared.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class VehicleCrashServicelmpl @Inject constructor(
    private val db: Database,
    private val notificationService: NotificationService,
    private val sessionManager: SessionManager,
) : VehicleCrashService {
    private val collection: Collection by lazy {
        db.getCollection("vehicle_crash")
            ?: throw IllegalStateException("Collection 'vehicle_crash' not found.")
    }
    private val ADMIN_ID = "SYSTEM_ADMIN_001"
    override suspend fun create(vehicleCrashDto: VehicleCrashDto): VehicleCrashDto {
        return try {
            val targetAdminId = sessionManager.adminIdFlow.firstOrNull() ?: ADMIN_ID
            val id = vehicleCrashDto.id ?: UUID.randomUUID().toString()
            val now = Date().toString()
            val status = vehicleCrashDto.status.takeIf { it.isNotBlank() } ?: "Pending"

            val vehicleToSave = vehicleCrashDto.copy(
                id = id,
                createdAt = now,
                lastUpdatedAt = now,
                status = status
            )

            val doc = MutableDocument(id).apply {
                setString("id", vehicleToSave.id)
                setString("userId", vehicleToSave.userId)
                val lat = vehicleToSave.latitude ?: 0.0
                val lng = vehicleToSave.longitude ?: 0.0
                setString("addressId", vehicleToSave.addressId)
                setDouble("latitude", lat)
                setDouble("longitude", lng)
                setString("reportDetails", vehicleToSave.reportDetails)
                setString("reportImage", vehicleToSave.reportImage)
                setString("reportVideo", vehicleToSave.reportVideo)
                setString("status", vehicleToSave.status)
                setString("createdAt", vehicleToSave.createdAt)
                setString("lastUpdatedAt", vehicleToSave.lastUpdatedAt)
                setString("createdById", vehicleToSave.createdById)
                setString("reviewById", vehicleToSave.reviewById)
                setString("lastUpdatedById", vehicleToSave.lastUpdatedById)
                setString("deletedById", vehicleToSave.deletedById)
                setString("deletedAt", vehicleToSave.deletedAt)
            }

            collection.save(doc)
            val details = vehicleToSave.reportDetails
            val previewText = if (details.length > 15) {
                "${details.take(15)}..."
            } else {
                details
            }
            val adminNotification = NotifyDto(
                sender = vehicleToSave.userId,
                receiver = targetAdminId,
                documentId = vehicleToSave.id,
                documentType = "VehicleCrashes",
                message = "New Vehicle Crashes Report: $previewText",
                createdAt = Clock.System.now()
            )
            notificationService.sendNotification(adminNotification)
            Log.d("VehicleCrashServiceImpl", "Created vehicle crash report: $vehicleToSave")
            vehicleToSave
        } catch (e: Exception) {
            Log.e("VehicleCrashServiceImpl", "Failed to create vehicle crash report: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getAll(userId: String?): List<VehicleCrashDto> {
        return try {
            val query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(collection))

            val results = query.execute().allResults()
            results.mapNotNull { result ->
                val dict = result.getDictionary(collection.name)
                dict?.let {
                    VehicleCrashDto(
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
            Log.e("VehicleCrashServiceImpl", "Failed to fetch vehicle crash reports: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getById(id: String): VehicleCrashDto? {
        return try {
            val doc = collection.getDocument(id) ?: return null
            Json { ignoreUnknownKeys = true }.decodeFromString<VehicleCrashDto>(doc.toJSON())
        } catch (e: Exception) {
            Log.e("VehicleCrashServiceImpl", "Failed to fetch VehicleCrashServiceImpl by id $id: ${e.message}")
            null
        }
    }

    override suspend fun update(id: String, vehicleCrashDto: VehicleCrashDto): VehicleCrashDto {
        return try {
            val doc = collection.getDocument(id)?.toMutable() ?: MutableDocument(id)

            val updated = vehicleCrashDto.copy(lastUpdatedAt = Date().toString())
            val jsonString = Json.encodeToString(VehicleCrashDto.serializer(), updated)

            doc.setJSON(jsonString)
            collection.save(doc)
            updated
        } catch (e: Exception) {
            Log.e("VehicleCrashServiceImpl", "Failed to update vehicle crash serviceImpl: ${e.message}", e)
            throw e
        }
    }

    override suspend fun delete(id: String) {
        try {
            val doc = collection.getDocument(id) ?: return
            collection.delete(doc)
            Log.d("VehicleCrashServiceImpl", "Deleted vehicle crash report with id: $id")
        } catch (e: Exception) {
            Log.e("VehicleCrashServiceImpl", "Failed to delete vehicle crash report: ${e.message}", e)
        }
    }
}