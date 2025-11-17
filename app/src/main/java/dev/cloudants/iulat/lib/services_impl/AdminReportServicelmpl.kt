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

class AdminReportServicelmpl @Inject constructor(
    private val db: Database
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
            val province = addressDict?.getString("province") ?: ""
            val municipality = addressDict?.getString("municipality") ?: ""
            val barangay = addressDict?.getString("barangay") ?: ""
            val latitude = addressDict?.getDouble("latitude") ?: 0.0
            val longitude = addressDict?.getDouble("longitude") ?: 0.0
            val address = "$barangay, $municipality, $province"
            usersMap[id] = Triple(name, email, "$latitude,$longitude")
        }

        fun queryCollection(collectionName: String, type: String): List<DashboardReportItemDto> {
            val col = db.getCollection(collectionName) ?: return emptyList()

            val query = QueryBuilder
                .select(
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

                list.add(
                    DashboardReportItemDto(
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
            finalList += queryCollection(collectionName, type)
        }

        return finalList.sortedByDescending { it.reportDate }
    }

    override suspend fun updateReportStatus(reportId: String, collectionName: String, newStatus: String) {
        val col = db.getCollection(collectionName) ?: return

        if (reportId.isEmpty()) {
            Log.e("AdminReportService", "Empty reportId, cannot update")
            return
        }

        val mutableDoc = col.getDocument(reportId)?.toMutable()
        if (mutableDoc == null) {
            Log.e("AdminReportService", "Document not found for reportId '$reportId' in collection '$collectionName'")
            return
        }

        mutableDoc.setString("status", newStatus)
        col.save(mutableDoc)
        Log.d("AdminReportService", "Updated report $reportId to status $newStatus")
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
                    SelectResult.property("reportId"),
                    SelectResult.property("reportDetails"),
                    SelectResult.property("createdAt"),
                    SelectResult.property("status"),
                    SelectResult.property("userId")
                )
                .from(DataSource.collection(col))
                .where(Expression.property("status").equalTo(Expression.string("Pending"))) // Only pending

            val result = query.execute()
            val list = mutableListOf<DashboardReportItemDto>()

            for (row in result) {
                val userId = row.getString("userId") ?: ""
                val userInfo = usersMap[userId] ?: Triple("Unknown User", "No Email", "No Location")

                list.add(
                    DashboardReportItemDto(
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

}

