package dev.cloudants.iulat.lib.services_impl

import android.util.Log
import com.couchbase.lite.Database
import com.couchbase.lite.MutableDocument
import dev.cloudants.iulat.lib.models.entities.MyLogsDto
import dev.cloudants.iulat.lib.services.MyLogsService
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.couchbase.lite.DataSource
import jakarta.inject.Inject
import kotlinx.serialization.json.Json

class MyLogsServiceImpl @Inject constructor(
    private val db: Database,
) : MyLogsService {
    private val myLogsCollection by lazy { db.getCollection("my_logs") ?: db.createCollection("my_logs") }

    override fun fetchAll(): List<MyLogsDto> {
        return try {
            QueryBuilder.select(SelectResult.all())
                .from(DataSource.collection(myLogsCollection))
                .execute()
                .allResults()
                .mapNotNull { result ->
                    result.getDictionary(myLogsCollection.name)?.let {
                        MyLogsDto(
                            id = it.getString("id"),
                            dateTimestamp = it.getString("dateTimestamp")
                        )
                    }
                }
        } catch (e: Exception) {
            Log.e("MyLogsServiceImpl", "Fetch all failed: ${e.message}")
            emptyList()
        }
    }

    override fun fetchOne(id: String): MyLogsDto? {
        return try {
            val doc = myLogsCollection.getDocument(id) ?: return null
            Json.decodeFromString<MyLogsDto>(doc.toJSON())
        } catch (e: Exception) {
            Log.e("MyLogsServiceImpl", "Fetch one failed: ${e.message}")
            null
        }
    }

    override fun logs(): MyLogsDto {
        Log.d("MyLogsServiceImpl", "logs() triggered...")
        val newId = java.util.UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis().toString()
        val log = MyLogsDto(id = newId, dateTimestamp = timestamp)

        return try {
            val doc = MutableDocument(newId)
            doc.setString("id", log.id)
            doc.setString("dateTimestamp", log.dateTimestamp)
            myLogsCollection.save(doc)
            log
        } catch (e: Exception) {
            Log.e("MyLogsServiceImpl", "Create failed: ${e.message}")
            throw e
        }
    }

}

