package dev.cloudants.iulat.lib.services_impl

import android.util.Log
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

    private fun getDocument(id: String): Document {
        val doc = collection.getDocument(id)
        if (doc == null) {
            throw NotFoundException("User not found: $id")
        }
        return doc
    }

    override fun findOne(id: String): UserDto {
        return fetchOne(id) ?: throw NotFoundException("User not found: $id")
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
        try {
            val freshId = UUID.randomUUID().toString()
            val userWithId = user.copy(id = freshId)
            val json = Json.Default.encodeToString(userWithId)
            val doc = MutableDocument(freshId)
            doc.setJSON(json)
            collection.save(doc)
            return userWithId
        } catch (e: Exception) {
            Log.e("UserServiceImpl", "Failed to create user: ${e.message}", e)
            throw e
        }
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

    override fun fetchOne(id: String): UserDto? {
        val doc = getDocument(id) ?: return null
        val json = doc.toJSON()
        return Json.decodeFromString<UserDto>(json)
    }

    override fun login(email: String, password: String): UserDto? {
        val query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
            .where(Expression.property("email").equalTo(Expression.string(email)))

        val result = query.execute().firstOrNull()
        if (result != null) {
            val userDict = result.getDictionary(collection.name)
            if (userDict == null) {
                Log.e("UserServiceImpl", "⚠ No dictionary found in result.")
                return null
            }

            val addressDict = userDict.getDictionary("address")
            val address = if (addressDict != null) {
                AddressDto(
                    province = addressDict.getString("province") ?: "",
                    municipality = addressDict.getString("municipality") ?: "",
                    barangay = addressDict.getString("barangay") ?: ""
                )
            } else AddressDto("", "", "")

            val user = UserDto(
                id = userDict.getString("id") ?: "",
                username = userDict.getString("username") ?: "",
                password = userDict.getString("password") ?: "",
                firstName = userDict.getString("firstName") ?: "",
                middleName = userDict.getString("middleName") ?: "",
                lastName = userDict.getString("lastName") ?: "",
                email = userDict.getString("email") ?: "",
                mobileNumber = userDict.getString("mobileNumber") ?: "",
                dateOfBirth = userDict.getString("dateOfBirth") ?: "",
                userProfile = userDict.getString("userProfile") ?: "",
                gender = userDict.getString("gender") ?: "",
                validId = userDict.getString("validId") ?: "",
                type = userDict.getString("type") ?: "",
                isAdmin = userDict.getBoolean("isAdmin"),
                isResidence = userDict.getBoolean("isResidence"),
                address = address
            )

            Log.e("UserServiceImpl", "Found user: ${user.email}")

            return if (user.password == password) {
                Log.e("UserServiceImpl", " Password matched for ${user.email}")
                user
            } else {
                Log.e("UserServiceImpl", " Incorrect password for ${user.email}")
                null
            }
        } else {
            Log.e("UserServiceImpl", "No user found for email: $email")
        }

        return null
    }

    override fun findByEmail(email: String): UserDto? {
        val query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
            .where(Expression.property("email").equalTo(Expression.string(email)))

        val result = query.execute().firstOrNull() ?: return null
        val userDict = result.getDictionary(collection.name) ?: return null

        val addressDict = userDict.getDictionary("address")
        val address = if (addressDict != null) {
            AddressDto(
                province = addressDict.getString("province") ?: "",
                municipality = addressDict.getString("municipality") ?: "",
                barangay = addressDict.getString("barangay") ?: ""
            )
        } else AddressDto("", "", "")

        return UserDto(
            id = userDict.getString("id") ?: "",
            username = userDict.getString("username") ?: "",
            password = userDict.getString("password") ?: "",
            firstName = userDict.getString("firstName") ?: "",
            middleName = userDict.getString("middleName") ?: "",
            lastName = userDict.getString("lastName") ?: "",
            email = userDict.getString("email") ?: "",
            mobileNumber = userDict.getString("mobileNumber") ?: "",
            dateOfBirth = userDict.getString("dateOfBirth") ?: "",
            userProfile = userDict.getString("userProfile") ?: "",
            gender = userDict.getString("gender") ?: "",
            validId = userDict.getString("validId") ?: "",
            type = userDict.getString("type") ?: "",
            isAdmin = userDict.getBoolean("isAdmin"),
            isResidence = userDict.getBoolean("isResidence"),
            address = address
        )
    }
}