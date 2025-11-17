package dev.cloudants.iulat.lib.services_impl

import android.util.Log
import com.couchbase.lite.*
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.services.ChatService
import dev.cloudants.iulat.lib.ui.message.model.ChatDto
import dev.cloudants.iulat.lib.ui.message.model.MessageDto
import dev.cloudants.iulat.lib.ui.message.model.UserChatDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.security.MessageDigest
import java.time.Instant
import com.couchbase.lite.Collection
import javax.inject.Inject

class ChatServiceImpl @Inject constructor(
    private val db: Database
) : ChatService {
    private val collectionChats: Collection by lazy {
        db.getCollection("chats")
            ?: throw IllegalStateException("Collection 'chats' not found.")
    }

    private val collectionMessages: Collection by lazy {
        db.getCollection("messages")
            ?: throw IllegalStateException("Collection 'messages' not found.")
    }

    private val collectionUsers: Collection by lazy {
        db.getCollection("users")
            ?: throw IllegalStateException("Collection 'users' not found.")
    }


    private fun generateChatId(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val fullHash = md.digest(input.toByteArray())
        val truncatedHash = fullHash.copyOfRange(0, 12)
        return truncatedHash.joinToString("") { "%02x".format(it) }
    }

    private fun getAdminId(sender: UserDto, receiver: UserDto) = if (sender.isAdmin) sender.id else receiver.id
    private fun getUserId(sender: UserDto, receiver: UserDto) = if (receiver.isResidence) receiver.id else sender.id

    override suspend fun findOneChatOrCreate(sender: UserDto, receiver: UserDto): ChatDto {
        val adminId = getAdminId(sender, receiver)
        val userId = getUserId(sender, receiver)
        val chatId = generateChatId("$adminId$userId")

        val existing = collectionChats.getDocument(chatId)

        val chatDto = ChatDto(
            id = existing?.getString("id") ?: chatId,
            user1Id = adminId!!,
            user2Id = userId!!,
            lastMessage = existing?.getString("lastMessage") ?: "",
            isRead = sender.isAdmin,
            updatedAt = existing?.getLong("updatedAt")?.let { Instant.ofEpochMilli(it) } ?: Instant.now()
        )

        if (existing == null) {
            val doc = MutableDocument(chatId).apply {
                setString("id", chatDto.id)
                setString("user1Id", chatDto.user1Id)
                setString("user2Id", chatDto.user2Id)
                setString("lastMessage", chatDto.lastMessage)
                setBoolean("isRead", chatDto.isRead)
                setLong("updatedAt", chatDto.updatedAt.toEpochMilli())
            }
            collectionChats.save(doc)
        } else if (sender.isAdmin) {
            val doc = existing.toMutable()
            doc.setBoolean("isRead", true)
            collectionChats.save(doc)
        }

        return chatDto
    }

    override suspend fun message(sender: UserDto, receiver: UserDto, content: String): MessageDto {
        try {
            val adminId = getAdminId(sender, receiver)
            val userId = getUserId(sender, receiver)
            val chatId = generateChatId("$adminId$userId")

            val messageId = generateChatId("${System.currentTimeMillis()}$chatId")
            val timestamp = Instant.now()

            val message = MutableDocument(messageId).apply {
                setString("id", messageId)
                setString("chatId", chatId)
                setString("senderId", sender.id!!)
                setString("receiverId", receiver.id!!)
                setString("content", content)
                setLong("createdAt", timestamp.toEpochMilli())
            }
            collectionMessages.save(message)

            val chatDoc = collectionChats.getDocument(chatId)?.toMutable() ?: MutableDocument(chatId)
            chatDoc.setString("lastMessage", content)
            chatDoc.setBoolean("isRead", sender.isAdmin)
            chatDoc.setLong("updatedAt", timestamp.toEpochMilli())
            collectionChats.save(chatDoc)

            return MessageDto(
                id = messageId,
                chatId = chatId,
                senderId = sender.id!!,
                receiverId = receiver.id!!,
                content = content,
                createdAt = timestamp
            )
        } catch (e: Exception) {
            Log.e("ChatServiceImpl", "Failed to send message: ${e.message}", e)
            throw e
        }
    }

    override fun fetchDirectMessages(sender: UserDto, receiver: UserDto): Flow<List<MessageDto>> = flow {
        val chatId = generateChatId("${getAdminId(sender, receiver)}${getUserId(sender, receiver)}")

        val query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collectionMessages))
            .where(Expression.property("chatId").equalTo(Expression.string(chatId)))
            .orderBy(Ordering.property("createdAt").descending())

        val results = query.execute()
        val messages = results.map { row ->
            val doc = row.getDictionary(collectionMessages.name)!!
            MessageDto(
                id = doc.getString("id")!!,
                chatId = doc.getString("chatId")!!,
                senderId = doc.getString("senderId")!!,
                receiverId = doc.getString("receiverId")!!,
                content = doc.getString("content")!!,
                createdAt = Instant.ofEpochMilli(doc.getLong("createdAt"))
            )
        }
        emit(messages)
    }

    override fun fetchUsers(userId: String): Flow<List<UserChatDto>> = flow {
        val query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collectionUsers))
            .where(Expression.property("id").notEqualTo(Expression.string(userId)))
        val results = query.execute()
        val users = results.map { row ->
            val doc = row.getDictionary(collectionUsers.name)!!
            val chatId = generateChatId("${userId}${doc.getString("id")}")
            val chatDoc = collectionChats.getDocument(chatId)

            UserChatDto(
                userDto = UserDto(
                    id = doc.getString("id"),
                    firstName = doc.getString("firstName") ?: "",
                    middleName = doc.getString("middleName"),
                    lastName = doc.getString("lastName") ?: "",
                    isAdmin = doc.getBoolean("isAdmin") ?: false,
                    isResidence = doc.getBoolean("isResidence") ?: false
                ),
                chatDto = ChatDto(
                    id = chatDoc?.getString("id") ?: chatId,
                    user1Id = chatDoc?.getString("user1Id") ?: userId,
                    user2Id = chatDoc?.getString("user2Id") ?: doc.getString("id")!!,
                    lastMessage = chatDoc?.getString("lastMessage") ?: "",
                    isRead = chatDoc?.getBoolean("isRead") ?: true,
                    updatedAt = chatDoc?.getLong("updatedAt")?.let { Instant.ofEpochMilli(it) } ?: Instant.now()
                ),
                updatedAt = chatDoc?.getLong("updatedAt")?.let { Instant.ofEpochMilli(it) }
            )
        }
        emit(users.sortedByDescending { it.updatedAt ?: Instant.EPOCH })
    }
}
