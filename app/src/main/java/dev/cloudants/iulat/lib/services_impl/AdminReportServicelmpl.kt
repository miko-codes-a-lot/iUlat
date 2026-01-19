package dev.cloudants.iulat.lib.services_impl

import android.util.Log
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Expression
import com.couchbase.lite.SelectResult
import com.couchbase.lite.QueryBuilder
import dev.cloudants.iulat.lib.models.entities.DashboardReportItemDto
import dev.cloudants.iulat.lib.services.AdminReportService
import jakarta.inject.Inject
import com.couchbase.lite.Meta
import com.couchbase.lite.MutableDocument
import dev.cloudants.iulat.lib.models.entities.TimelineEventDto
import com.couchbase.lite.Function
import com.couchbase.lite.Ordering
import dev.cloudants.iulat.lib.models.entities.NotifyDto
import dev.cloudants.iulat.lib.services.NotificationService
import dev.cloudants.iulat.shared.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock

class AdminReportServicelmpl @Inject constructor(
    private val db: Database,
    private val notificationService: NotificationService,
    private val sessionManager: SessionManager
) : AdminReportService {

    override suspend fun getAllReports(): List<DashboardReportItemDto> {
        val finalList = mutableListOf<DashboardReportItemDto>()

        val usersCol = db.getCollection("users") ?: return emptyList()
        val usersMap = mutableMapOf<String, Triple<String, String, String>>()

        val userQuery = QueryBuilder
            .select(
                SelectResult.property("id"),
                SelectResult.property("firstName"),
                SelectResult.property("middleName"),
                SelectResult.property("lastName"),
                SelectResult.property("email"),
                SelectResult.property("address")
            )
            .from(DataSource.collection(usersCol))

        val userResult = userQuery.execute()
        for (row in userResult) {
            val id = row.getString("id") ?: continue
            val firstName = row.getString("firstName") ?: ""
            val middleName = row.getString("middleName")?.let { " $it" } ?: ""
            val lastName = row.getString("lastName") ?: ""
            val name = "$firstName$middleName $lastName".trim()
            val email = row.getString("email") ?: "No Email"
            val addressDict = row.getDictionary("address")
            val zone = addressDict?.getString("zone") ?: ""
//            val province = addressDict?.getString("province") ?: ""
//            val municipality = addressDict?.getString("municipality") ?: ""
//            val barangay = addressDict?.getString("barangay") ?: ""
//            val latitude = addressDict?.getDouble("latitude") ?: 0.0
//            val longitude = addressDict?.getDouble("longitude") ?: 0.0
//            val address = "$barangay, $municipality, $province"
//            usersMap[id] = Triple(name, email, "$latitude,$longitude")
            usersMap[id] = Triple(name, email, zone)
        }

        fun queryCollection(collectionName: String, type: String): List<DashboardReportItemDto> {
            val col = db.getCollection(collectionName) ?: return emptyList()

            val query = QueryBuilder
                .select(
                    SelectResult.expression(Meta.id).`as`("docId"),
                    SelectResult.property("reportId"),
                    SelectResult.property("reportDetails"),
                    SelectResult.property("createdAt"),
                    SelectResult.property("status"),
                    SelectResult.property("userId")
                )
                .from(DataSource.collection(col))

            val result = query.execute()
            val list = mutableListOf<DashboardReportItemDto>()

            for (row in result) {
                val userId = row.getString("userId") ?: ""
                val userInfo = usersMap[userId] ?: Triple("Unknown User", "No Email", "No Location")
                val docId = row.getString("docId")!!
                val fetchedReportId = row.getString("reportId")
                val finalReportId = if (fetchedReportId.isNullOrEmpty()) docId else fetchedReportId
                list.add(
                    DashboardReportItemDto(
                        docId = row.getString("docId")!!,
                        reportId = finalReportId,
                        reportType = type,
                        reportDetails = row.getString("reportDetails") ?: "",
                        reportDate = row.getString("createdAt") ?: "",
                        status = row.getString("status") ?: "",
                        userName = userInfo.first,
                        userEmail = userInfo.second,
                        addressId = userInfo.third
                    )
                )
            }

            return list
        }

        val collections = mapOf(
            "garbage_disposal" to "Garbage Disposal",
            "broken_streetlights" to "Broken Streetlights",
            "no_water_supply" to "No Water Supply",
            "others" to "Others",
            "public_disturbance" to "Public Disturbance",
            "road_repair" to "Road Repair",
            "robberies" to "Robberies",
            "vehicle_crash" to "Vehicle Crash"
        )

        for ((collectionName, type) in collections) {
            finalList += queryCollection(collectionName, type)
        }

        return finalList.sortedByDescending { it.reportDate }
    }

    override suspend fun updateReportStatus(docId: String, collectionName: String, newStatus: String) {
        val col = db.getCollection(collectionName) ?: return
        val doc = col.getDocument(docId) ?: return
        val mutableDoc = doc.toMutable()

        val receiverUserId = doc.getString("userId") ?: ""
        val adminId = sessionManager.userIdFlow.firstOrNull() ?: "SYSTEM_ADMIN_001"

        mutableDoc.setString("status", newStatus)
        mutableDoc.setString("lastUpdatedById", adminId)
        mutableDoc.setString("lastUpdatedAt", java.time.Instant.now().toString())

        try {
            col.save(mutableDoc)
            Log.d("AdminReportService", "SUCCESS: Status saved for $docId")
            if (receiverUserId.isNotEmpty()) {
                val userNotification = NotifyDto(
                    sender = adminId,
                    receiver = receiverUserId,
                    documentId = docId,
                    documentType = collectionName.split("_")
                        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
                    message = "Your report status has been updated to: $newStatus",
                    createdAt = Clock.System.now()
                )
                notificationService.sendNotification(userNotification)
                Log.d("AdminReportService", "Notification sent to user: $receiverUserId")
            }
        } catch (e: Exception) {
            Log.e("AdminReportService", "ERROR: ${e.message}")
        }

    }


    override suspend fun getPendingReports(): List<DashboardReportItemDto> {
        val finalList = mutableListOf<DashboardReportItemDto>()

        val usersCol = db.getCollection("users") ?: return emptyList()
        val usersMap = mutableMapOf<String, Triple<String, String, String>>()

        val userQuery = QueryBuilder
            .select(
                SelectResult.property("id"),
                SelectResult.property("firstName"),
                SelectResult.property("middleName"),
                SelectResult.property("lastName"),
                SelectResult.property("email"),
                SelectResult.property("address")
            )
            .from(DataSource.collection(usersCol))

        val userResult = userQuery.execute()
        for (row in userResult) {
            val id = row.getString("id") ?: continue
            val firstName = row.getString("firstName") ?: ""
            val middleName = row.getString("middleName")?.let { " $it" } ?: ""
            val lastName = row.getString("lastName") ?: ""
            val name = "$firstName$middleName $lastName".trim()
            val email = row.getString("email") ?: "No Email"
            val address = row.getString("address") ?: "No Location"
            usersMap[id] = Triple(name.ifBlank { "Unknown User" }, email, address)
        }

        fun queryCollectionPending(collectionName: String, type: String): List<DashboardReportItemDto> {
            val col = db.getCollection(collectionName) ?: return emptyList()

            val query = QueryBuilder
                .select(
                    SelectResult.expression(Meta.id).`as`("docId"),
                    SelectResult.property("reportId"),
                    SelectResult.property("reportDetails"),
                    SelectResult.property("createdAt"),
                    SelectResult.property("status"),
                    SelectResult.property("userId")
                )
                .from(DataSource.collection(col))
                .where(Expression.property("status").equalTo(Expression.string("Pending")))

            val result = query.execute()
            val list = mutableListOf<DashboardReportItemDto>()

            for (row in result) {
                val userId = row.getString("userId") ?: ""
                val userInfo = usersMap[userId] ?: Triple("Unknown User", "No Email", "No Location")

                list.add(
                    DashboardReportItemDto(
                        docId = row.getString("docId")!!,
                        reportId = row.getString("reportId") ?: "",
                        reportType = type,
                        reportDetails = row.getString("reportDetails") ?: "",
                        reportDate = row.getString("createdAt") ?: "",
                        status = row.getString("status") ?: "",
                        userName = userInfo.first,
                        userEmail = userInfo.second,
                        addressId = userInfo.third
                    )
                )
            }

            return list
        }

        val collections = mapOf(
            "garbage_disposal" to "Garbage Disposal",
            "broken_streetlights" to "Broken Streetlights",
            "no_water_supply" to "No Water Supply",
            "others" to "Others",
            "public_disturbance" to "Public Disturbance",
            "road_repair" to "Road Repair",
            "robberies" to "Robberies",
            "vehicle_crash" to "Vehicle Crash"
        )

        for ((collectionName, type) in collections) {
            finalList += queryCollectionPending(collectionName, type)
        }

        return finalList.sortedByDescending { it.reportDate }
    }

    override suspend fun getReportsByStatus(status: String, search: String ): List<DashboardReportItemDto> {

        val finalList = mutableListOf<DashboardReportItemDto>()

        val usersCol = db.getCollection("users") ?: return emptyList()
        val usersMap = mutableMapOf<String, Triple<String, String, String>>()

        val userQuery = QueryBuilder
            .select(
                SelectResult.property("id"),
                SelectResult.property("firstName"),
                SelectResult.property("middleName"),
                SelectResult.property("lastName"),
                SelectResult.property("email"),
                SelectResult.property("address")
            )
            .from(DataSource.collection(usersCol))

        val userResult = userQuery.execute()
        for (row in userResult) {
            val id = row.getString("id") ?: continue
            val firstName = row.getString("firstName") ?: ""
            val middleName = row.getString("middleName")?.let { " $it" } ?: ""
            val lastName = row.getString("lastName") ?: ""
            val name = "$firstName$middleName $lastName".trim()
            val email = row.getString("email") ?: "No Email"
            val addressDict = row.getDictionary("address")
            val province = addressDict?.getString("province") ?: ""
            val municipality = addressDict?.getString("municipality") ?: ""
            val barangay = addressDict?.getString("barangay") ?: ""
            val latitude = addressDict?.getDouble("latitude") ?: 0.0
            val longitude = addressDict?.getDouble("longitude") ?: 0.0
            val address = "$barangay, $municipality, $province"
            usersMap[id] = Triple(name, email, "$latitude,$longitude")
        }

        fun queryFiltered(collectionName: String, type: String): List<DashboardReportItemDto> {
            val col = db.getCollection(collectionName) ?: return emptyList()

            var whereExp = Expression.booleanValue(true)
            whereExp = whereExp.and(
                Expression.property("status").equalTo(Expression.string(status))
            )

            if (search.isNotEmpty()) {
                whereExp = whereExp.and(
                    Expression.property("reportDetails")
                        .like(Expression.string("%$search%"))
                )
            }

            val query = QueryBuilder
                .select(
                    SelectResult.expression(Meta.id).`as`("docId"),
                    SelectResult.property("reportId"),
                    SelectResult.property("reportDetails"),
                    SelectResult.property("createdAt"),
                    SelectResult.property("status"),
                    SelectResult.property("userId")
                )
                .from(DataSource.collection(col))
                .where(whereExp)

            val result = query.execute()
            val list = mutableListOf<DashboardReportItemDto>()

            for (row in result) {
                val userId = row.getString("userId") ?: ""
                val userInfo = usersMap[userId] ?: Triple("Unknown User", "No Email", "No Location")

                list.add(
                    DashboardReportItemDto(
                        docId = row.getString("docId")!!,
                        reportId = row.getString("reportId") ?: "",
                        reportType = type,
                        reportDetails = row.getString("reportDetails") ?: "",
                        reportDate = row.getString("createdAt") ?: "",
                        status = row.getString("status") ?: "",
                        userName = userInfo.first,
                        userEmail = userInfo.second,
                        addressId = userInfo.third
                    )
                )
            }
            return list
        }

        val collections = mapOf(
            "garbage_disposal" to "Garbage Disposal",
            "broken_streetlights" to "Broken Streetlights",
            "no_water_supply" to "No Water Supply",
            "others" to "Others",
            "public_disturbance" to "Public Disturbance",
            "road_repair" to "Road Repair",
            "robberies" to "Robberies",
            "vehicle_crash" to "Vehicle Crash"
        )

        for ((collectionName, type) in collections) {
            finalList += queryFiltered(collectionName, type)
        }

        val sdf = java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.ENGLISH)

        return finalList.sortedByDescending {
            try {
                sdf.parse(it.reportDate)?.time ?: 0L
            } catch (e: Exception) {
                0L
            }
        }
    }

    override suspend fun getTimelineEvents(reportId: String): List<TimelineEventDto> {
        val col = db.getCollection("timeline_events") ?: return emptyList()

        val query = QueryBuilder
            .select(
                SelectResult.expression(Meta.id).`as`("docId"),
                SelectResult.property("userId"),
                SelectResult.property("status"),
                SelectResult.property("reportId"),
                SelectResult.property("time"),
                SelectResult.property("date"),
                SelectResult.property("message")
            )
            .from(DataSource.collection(col))
            .where(Expression.property("reportId").equalTo(Expression.string(reportId)))
            .orderBy(Ordering.property("createdAt").ascending())
        val result = query.execute()
        val list = mutableListOf<TimelineEventDto>()

        for (row in result) {
            list.add(
                TimelineEventDto(
                    docId = row.getString("docId")!!,
                    userId = row.getString("userId") ?: "",
                    status = row.getString("status") ?: "",
                    reportId = row.getString("reportId") ?: "",
                    time = row.getString("time") ?: "",
                    date = row.getString("date") ?: "",
                    message = row.getString("message") ?: ""
                )
            )
        }

        return list
    }

    override suspend fun saveTimelineMessage(timelineEvent: TimelineEventDto) {
        val col = db.getCollection("timeline_events") ?: return

        val mutableDoc = MutableDocument()
        mutableDoc.setString("userId", timelineEvent.userId)
        mutableDoc.setString("status", timelineEvent.status)
        mutableDoc.setString("reportId", timelineEvent.reportId)
        mutableDoc.setString("time", timelineEvent.time)
        mutableDoc.setString("date", timelineEvent.date)
        mutableDoc.setString("message", timelineEvent.message)
        mutableDoc.setString("createdAt", java.time.Instant.now().toString())
        try {
            col.save(mutableDoc)
            Log.d("AdminReportService", "Timeline message saved successfully")
        } catch (e: Exception) {
            Log.e("AdminReportService", "Error saving timeline message: ${e.message}")
        }
    }

    override suspend fun deleteReportsWithNoStatus() {
        val collections = listOf(
            "garbage_disposal",
            "broken_streetlights",
            "no_water_supply",
            "others",
            "public_disturbance",
            "road_repair",
            "robberies",
            "vehicle_crash"
        )

        for (collectionName in collections) {
            val col = db.getCollection(collectionName) ?: continue

            val query = QueryBuilder
                .select(SelectResult.expression(Meta.id).`as`("docId"))
                .from(DataSource.collection(col))
                .where(
                    Expression.property("status").equalTo(Expression.string(""))
                        .or(Expression.property("status").equalTo(Expression.value(null)))
                )

            val result = query.execute()
            for (row in result) {
                val docId = row.getString("docId") ?: continue
                col.getDocument(docId)?.let { doc ->
                    try {
                        col.delete(doc)
                        Log.d("AdminReportService", "Deleted report with no status: $docId")
                    } catch (e: Exception) {
                        Log.e("AdminReportService", "Failed to delete report $docId: ${e.message}")
                    }
                }
            }
        }
    }

    override suspend fun getReportPercentages(): Map<String, Float> {
        val collections = mapOf(
            "garbage_disposal" to "Garbage Disposal",
            "broken_streetlights" to "Broken Streetlights",
            "no_water_supply" to "No Water Supply",
            "others" to "Others",
            "public_disturbance" to "Public Disturbance",
            "road_repair" to "Road Repair",
            "robberies" to "Robberies",
            "vehicle_crash" to "Vehicle Crashes"
        )

        val counts = mutableMapOf<String, Long>()
        var totalReports = 0L

        for ((colName, label) in collections) {
            val col = db.getCollection(colName) ?: continue
            val query = QueryBuilder
                .select(SelectResult.expression(Function.count(Expression.all())))
                .from(DataSource.collection(col))

            try {
                val result = query.execute().allResults()
                if (result.isNotEmpty()) {
                    val count = result[0].getInt(0).toLong()
                    counts[label] = count
                    totalReports += count
                }
            } catch (e: Exception) {
                Log.e("AdminReportService", "Error counting $colName: ${e.message}")
                counts[label] = 0
            }
        }

        val percentages = mutableMapOf<String, Float>()
        if (totalReports > 0) {
            for ((label, count) in counts) {
                val percentage = (count.toFloat() / totalReports.toFloat()) * 100f
                percentages[label] = percentage
            }
        } else {
            for ((_, label) in collections) {
                percentages[label] = 0f
            }
        }
        return percentages
    }

    override suspend fun getReportCounts(): Map<String, Int> {
        val collections = mapOf(
            "garbage_disposal" to "Garbage Disposal",
            "broken_streetlights" to "Broken Streetlights",
            "no_water_supply" to "No Water Supply",
            "others" to "Others",
            "public_disturbance" to "Public Disturbance",
            "road_repair" to "Road Repair",
            "robberies" to "Robberies",
            "vehicle_crash" to "Vehicle Crashes"
        )

        val counts = mutableMapOf<String, Int>()
        for ((colName, label) in collections) {
            val col = db.getCollection(colName) ?: continue
            val query = QueryBuilder
                .select(SelectResult.expression(Function.count(Expression.all())))
                .from(DataSource.collection(col))

            try {
                val result = query.execute().allResults()
                if (result.isNotEmpty()) {
                    val count = result[0].getInt(0)
                    counts[label] = count
                } else {
                    counts[label] = 0
                }
            } catch (e: Exception) {
                Log.e("AdminReportService", "Error counting $colName: ${e.message}")
                counts[label] = 0
            }
        }
        return counts
    }

}

