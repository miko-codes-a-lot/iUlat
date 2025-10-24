package dev.cloudants.iulat.lib.services

import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Document
import com.couchbase.lite.Expression
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import dev.cloudants.iulat.lib.exceptions.NotFoundException
import dev.cloudants.iulat.lib.models.entities.UserDto
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

class UserServiceImpl @Inject constructor (
    private val db: Database,
): UserService {
    private val collection by lazy {
        db.getCollection("users")
            ?: throw IllegalStateException("Collection 'users' not found")
    }

    private fun getDocument(id: String): Document {
        val doc = collection.getDocument(id)
        if (doc == null) {
            throw NotFoundException("User not found: $id")
        }
        return doc
    }

    override fun findOne(id: String): UserDto {
        val doc = this.getDocument(id)

        return Json.decodeFromString<UserDto>(doc.toJSON())
    }

    override fun findAll(): List<UserDto> {
        return QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
            .where(
                Expression.property("type")
                    .equalTo(Expression.string("user")
                    )
            )
            .execute()
            .allResults()
            .map { d -> Json.decodeFromString<UserDto>(d.toJSON()) }
    }

    override fun create(user: UserDto): UserDto {
        val freshId = UUID.randomUUID().toString()
        val doc = MutableDocument(freshId)
        doc.setJSON(Json.encodeToString(user))
        collection.save(doc)
        return user.copy(id = freshId)
    }

    override fun update(id: String, user: UserDto): UserDto {
        val doc = this.getDocument(id)

        val mutableDoc = doc.toMutable()
        mutableDoc.setJSON(Json.encodeToString(user))
        collection.save(mutableDoc)

        return user
    }

    override fun delete(id: String): Boolean {
        val doc = this.getDocument(id)
        try {
            collection.delete(doc)

            return true
        } catch(e: Exception) {
            return false
        }
    }
}