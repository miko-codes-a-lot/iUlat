package dev.cloudants.iulat.lib.services_impl

import android.util.Base64
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
import org.mindrot.jbcrypt.BCrypt
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
        collection
        logAllUsers()
    }
    private fun getDocument(id: String): Document {
        val doc = collection.getDocument(id)
        if (doc == null) {
            throw NotFoundException("User not found: $id")
        }
        return doc
    }

    fun logAllUsers() {
        try {
            val results = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(collection))
                .execute()
                .allResults()

            results.forEach { result ->
                val dict = result.getDictionary(collection.name)
                Log.d("UserServiceImpl", "User doc: ${dict?.toJSON()}")
            }

            Log.d("UserServiceImpl", "Total users: ${results.size}")
        } catch (e: Exception) {
            Log.e("UserServiceImpl", "Failed to fetch all users: ${e.message}")
        }
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
            .mapNotNull { result ->
                val userDict = result.getDictionary(collection.name)
                userDict?.let { dict ->
                    try {
                        Json.Default.decodeFromString<UserDto>(dict.toJSON())
                    } catch (e: Exception) {
                        Log.e("UserServiceImpl", "Error decoding UserDto from query result: ${e.message}")
                        null
                    }
                }
            }
    }

    override fun createAdminUser(user: UserDto): UserDto {
        return try {
            val freshId = UUID.randomUUID().toString()
            val adminUser = user.copy(
                id = freshId,
                isAdmin = true,
                type = "admin"
            )
            val json = Json.encodeToString(adminUser)
            val doc = MutableDocument(freshId).apply { setJSON(json) }
            collection.save(doc)
            Log.e("UserServiceImpl", "Admin user created successfully: ${adminUser.email}")
            adminUser
        } catch (e: Exception) {
            Log.e("UserServiceImpl", "Failed to create admin user: ${e.message}", e)
            throw e
        }
    }

    override fun create(user: UserDto): UserDto {
        try {
            val freshId = UUID.randomUUID().toString()
            val hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())
            val userWithHashedPassword = user.copy(id = freshId,password = hashedPassword)
            val json = Json.Default.encodeToString(userWithHashedPassword)
            val doc = MutableDocument(freshId).apply { setJSON(json) }
            collection.save(doc)
            Log.e("UserServiceImpl", "User created successfully: ${user.email}")
            return userWithHashedPassword
        } catch (e: Exception) {
            Log.e("UserServiceImpl", "Failed to create user: ${e.message}", e)
            throw e
        }
    }


    override fun update(id: String, user: UserDto): UserDto {
        try {
            val doc = this.getDocument(id)
            val isHashedAlready = user.password.startsWith("\$2a\$") || user.password.startsWith("\$2b\$")
            val finalPassword = if (isHashedAlready) user.password else BCrypt.hashpw(user.password, BCrypt.gensalt())
            val userToSave = user.copy(id = id, password = finalPassword)
            val json = Json.Default.encodeToString(userToSave)

            val mutableDoc = doc.toMutable()
            mutableDoc.setJSON(json)
            collection.save(mutableDoc)

            return userToSave
        } catch (e: Exception) {
            Log.e("UserServiceImpl", "Failed to update user: ${e.message}", e)
            throw e
        }
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
                Log.e("UserServiceImpl", " No dictionary found in result.")
                return null
            }

            val addressDict = userDict.getDictionary("address")
//            val address = if (addressDict != null) {
//                AddressDto(
//                    province = addressDict.getString("province") ?: "",
//                    municipality = addressDict.getString("municipality") ?: "",
//                    barangay = addressDict.getString("barangay") ?: ""
//                )
//            } else AddressDto("", "", "")
            val address = AddressDto("","","","","",0.0, 0.0)

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

            return if (BCrypt.checkpw(password, user.password)) {
//          return if (user.password == password) {
                Log.e("UserServiceImpl", "Password matched for ${user.email}")
                user
            } else {
                Log.e("UserServiceImpl", "Incorrect password for ${user.email}")
                null
            }
        } else {
            Log.e("UserServiceImpl", "No user found for email: $email")
        }

        return null
    }

    override fun saveZonesToDatabase(zones: List<AddressDto>): Boolean {
        return try {
            zones.forEach { address ->
                val id = address.id?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()

                val doc = MutableDocument(id).apply {
                    setString("id", id)
                    setString("province", address.province)
                    setString("municipality", address.municipality)
                    setString("barangay", address.barangay)
                    setString("zone", address.zone)
                    setDouble("latitude", address.latitude)
                    setDouble("longitude", address.longitude)
                }
                collection.save(doc)
                Log.e("AddressInit", "Saved address: $id -> ${address.zone}, ${address.barangay}")
            }

            Log.e("AddressInit", "Inserted all zones successfully.")
            true
        } catch (e: Exception) {
            Log.e("AddressInit", "Failed to insert zones: ${e.message}")
            false
        }
    }

    override fun saveValidId(userId: String, imageUri: ByteArray?): Result<UserDto> {
        return try {
            val userDoc = getDocument(userId)
            val user = Json.decodeFromString<UserDto>(userDoc.toJSON())
            if (imageUri != null) {
                val base64Image = Base64.encodeToString(imageUri, Base64.DEFAULT)
                val updatedUser = user.copy(imageBase64 = base64Image)

                val json = Json.encodeToString(updatedUser)
                val mutableDoc = userDoc.toMutable().apply { setJSON(json) }
                collection.save(mutableDoc)
                Result.success(updatedUser)
            } else {
                Result.failure(Exception("No image provided"))
            }
        } catch (e: Exception) {
            Log.e("UserServiceImpl", "Failed to save valid ID: ${e.message}", e)
            Result.failure(e)
        }
    }

    override fun findByEmail(email: String): UserDto? {
        val query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
            .where(Expression.property("email").equalTo(Expression.string(email)))

        val result = query.execute().firstOrNull() ?: return null
        val userDict = result.getDictionary(collection.name) ?: return null

//        val addressDict = userDict.getDictionary("address")
//        val address = if (addressDict != null) {
//            AddressDto(
//                province = addressDict.getString("province") ?: "",
//                municipality = addressDict.getString("municipality") ?: "",
//                barangay = addressDict.getString("barangay") ?: ""
//            )
//        } else AddressDto("", "", "")
        val addres = AddressDto("","","","","",0.0, 0.0)
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
            address = addres
        )
    }
}