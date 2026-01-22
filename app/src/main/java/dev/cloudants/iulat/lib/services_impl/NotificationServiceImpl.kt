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
    private val notificationCollection: Collection by lazy {
        db.getCollection("notifications") ?: db.createCollection("notifications")
    }

    override fun getNotificationsStream(userId: String): Flow<List<NotifyDto>> = callbackFlow {
        val query = QueryBuilder.select(SelectResult.all(), SelectResult.expression(Meta.id))
            .from(DataSource.collection(notificationCollection))
            .where(Expression.property("receiver").equalTo(Expression.string(userId)))

        val listener = query.addChangeListener { change ->
            val list = change.results?.mapNotNull { result ->
                val dict = result.getDictionary("notifications")
                dict?.let {
                    NotifyDto(
                        id = result.getString("id"),
                        sender = it.getString("sender") ?: "",
                        receiver = it.getString("receiver") ?: "",
                        message = it.getString("message") ?: "",
                        documentId = it.getString("documentId"),
                        documentType = it.getString("documentType"),
                        read = it.getBoolean("read"),
                        createdAt = it.getString("createdAt")?.let { dateStr ->
                            try { Instant.parse(dateStr) } catch (e: Exception) { null }
                        }
                    )
                }
            }
            trySend(list?.sortedByDescending { it.createdAt } ?: emptyList())
        }
        awaitClose { query.removeChangeListener(listener) }
    }

    override suspend fun sendNotification(notification: NotifyDto) = withContext(Dispatchers.IO) {
        val mutableDoc = MutableDocument()
        mutableDoc.setString("type", "NotifyDto")
        mutableDoc.setString("sender", notification.sender)
        mutableDoc.setString("receiver", notification.receiver)
        mutableDoc.setString("documentId", notification.documentId)
        mutableDoc.setString("documentType", notification.documentType)
        mutableDoc.setString("message", notification.message)
        mutableDoc.setBoolean("read", false)
        mutableDoc.setString("createdAt", kotlinx.datetime.Clock.System.now().toString())
        notificationCollection.save(mutableDoc)
    }

    override suspend fun markAsRead(notificationId: String): Unit = withContext(Dispatchers.IO) {
        notificationCollection.getDocument(notificationId)?.toMutable()?.let {
            it.setBoolean("read", true)
            notificationCollection.save(it)
        }
    }

    override suspend fun broadcastAnnouncement(senderId: String, title: String, message: String) = withContext(Dispatchers.IO) {
        val userCollection = db.getCollection("users") ?: return@withContext

        val query = QueryBuilder.select(SelectResult.expression(Meta.id))
            .from(DataSource.collection(userCollection))
            .where(Expression.property("id").notEqualTo(Expression.string(senderId)))
        val results = query.execute()

        results.allResults().forEach { result ->
            val userId = result.getString("id")
            if (userId != null) {
                val mutableDoc = MutableDocument()
                mutableDoc.setString("type", "NotifyDto")
                mutableDoc.setString("sender", senderId)
                mutableDoc.setString("receiver", userId)
                mutableDoc.setString("documentType", title)
                mutableDoc.setString("message", message)
                mutableDoc.setBoolean("read", false)
                mutableDoc.setString("createdAt", kotlinx.datetime.Clock.System.now().toString())

                notificationCollection.save(mutableDoc)
            }
        }
    }
}