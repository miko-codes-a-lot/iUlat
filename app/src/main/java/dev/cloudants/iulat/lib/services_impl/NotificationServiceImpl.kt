package dev.cloudants.iulat.lib.services_impl

import com.couchbase.lite.*
import dev.cloudants.iulat.lib.services.NotificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import dev.cloudants.iulat.lib.models.entities.NotifyDto
import kotlinx.coroutines.channels.awaitClose
import android.util.Log
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.couchbase.lite.Collection
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import kotlinx.datetime.Instant
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NotificationServiceImpl @Inject constructor(
    private val db: Database,
) : NotificationService {
    private val notificationCollectionName = "notifications"
    private val notificationCollection: Collection by lazy {
        db.getCollection(notificationCollectionName)
            ?: throw IllegalStateException("Collection 'notifications' not found.")
    }

    override fun getNotificationsStream(userId: String): Flow<List<NotifyDto>> = callbackFlow {
        val query = QueryBuilder
            .select(SelectResult.all())
            .from(DataSource.collection(notificationCollection))
            .where(
                Expression.property("type").equalTo(Expression.string("NotifyDto"))
//      comment this line for testing             .and(Expression.property("receiver").equalTo(Expression.string(userId)))
            )
//      comment this line for testing      .orderBy(Ordering.property("createdAt").descending())

        val listenerToken = query.addChangeListener { change ->
            val notificationList = change.results
                ?.allResults()
                ?.map { result ->
                    mapResultToNotifyDto(result)
                }
                ?: emptyList()

            trySend(notificationList)
            if (change.error != null) {
                Log.e("CouchbaseQuery", "Query failed: ${change.error}")
            }
            // add new rows
            val rows = change.results?.allResults()?.size ?: 0
            Log.d("NotificationDebug", "Found $rows notifications in DB")
        }

        awaitClose {
            query.removeChangeListener(listenerToken)
        }
    }

    override suspend fun markAsRead(notificationId: String) {
        withContext(Dispatchers.IO) {
            try {
                val document = notificationCollection.getDocument(notificationId)
                if (document != null) {
                    val mutableDoc = document.toMutable()
                    mutableDoc.setBoolean("read", true)

                    notificationCollection.save(mutableDoc)
                }
            } catch (e: CouchbaseLiteException) {
                Log.e("Couchbase", "Failed to mark notification $notificationId as read", e)
                throw e
            }
        }
    }

    private fun mapResultToNotifyDto(result: Result): NotifyDto {
        val data = result.getDictionary(notificationCollectionName)
            ?: result.getDictionary(0)
            ?: result
        return NotifyDto(
            id = data?.getString("id"),
            sender = data?.getString("sender") ?: "",
            receiver = data?.getString("receiver") ?: "",
            documentId = data?.getString("documentId"),
            documentType = data?.getString("documentType"),
            message = data?.getString("message") ?: "",
            read = data?.getBoolean("read") ?: false,
            createdAt = data?.getString("createdAt")?.let { Instant.parse(it) }
        )
    }
}