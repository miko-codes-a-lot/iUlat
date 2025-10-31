package dev.cloudants.iulat.lib.services_impl

import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Document
import com.couchbase.lite.Expression
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import dev.cloudants.iulat.lib.exceptions.NotFoundException
import dev.cloudants.iulat.lib.models.entities.AddressDto
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.services.UserService
import kotlinx.serialization.encodeToString
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

    init {
        initSampleDataIfNeeded()
    }

    private fun insertSampleData() {
        val sampleUsers = listOf(
            UserDto(
                id = null,
                username = "admin",
                password = "admin123",
                firstName = "Admin",
                middleName = null,
                lastName = "User",
                email = "admin@example.com",
                mobileNumber = "1234567890",
                gender = "Male",
                address = AddressDto("Occidental Mindoro", "San Jose", "Santolan"),
                type = "user",
                role = "admin"
            ),
            UserDto(
                id = null,
                username = "user",
                password = "user123",
                firstName = "Regular",
                middleName = null,
                lastName = "User",
                email = "user@example.com",
                mobileNumber = "0987654321",
                gender = "Female",
                address = AddressDto("Occidental Mindoro", "San Jose", "Mapua"),
                type = "user",
                role = "user"
            )
        )

        sampleUsers.forEach { user ->
            val freshId = UUID.randomUUID().toString()
            val doc = MutableDocument(freshId)
            doc.setJSON(Json.Default.encodeToString(user))
            collection.save(doc)
        }
    }

    fun initSampleDataIfNeeded() {
        if (collection.count.toInt() == 0) {
            insertSampleData()
        }
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

        return Json.Default.decodeFromString<UserDto>(doc.toJSON())
    }

    override fun findAll(): List<UserDto> {
        return QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
            .where(
                Expression.property("type")
                    .equalTo(
                        Expression.string("user")
                    )
            )
            .execute()
            .allResults()
            .map { d -> Json.Default.decodeFromString<UserDto>(d.toJSON()) }
    }

    override fun create(user: UserDto): UserDto {
        val freshId = UUID.randomUUID().toString()
        val doc = MutableDocument(freshId)
        doc.setJSON(Json.Default.encodeToString(user))
        collection.save(doc)
        return user.copy(id = freshId)
    }

    override fun update(id: String, user: UserDto): UserDto {
        val doc = this.getDocument(id)

        val mutableDoc = doc.toMutable()
        mutableDoc.setJSON(Json.Default.encodeToString(user))
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

    fun login(username: String, password: String): UserDto? {
        val query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
            .where(
                Expression.property("username")
                    .equalTo(Expression.string(username))
            )

        val result = query.execute().firstOrNull()

        if (result != null) {
            val user = Json.Default.decodeFromString<UserDto>(result.toJSON())
            return if (user.password == password) {
                user
            } else {
                null
            }
        }
        return null
    }

}