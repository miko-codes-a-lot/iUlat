package dev.cloudants.iulat.lib.services_impl
import android.util.Log
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.couchbase.lite.DataSource

import com.couchbase.lite.Collection
import com.couchbase.lite.Database
import dev.cloudants.iulat.lib.models.entities.AddressDto
import dev.cloudants.iulat.lib.services.AddressService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

class AddressServiceImpl @Inject constructor(
    private val db: Database,
) : AddressService {
    private val collection by lazy { db.getCollection("address") ?: db.createCollection("address") }

    override fun fetchAll(): List<AddressDto> {
        return try {
            QueryBuilder.select(SelectResult.all())
                .from(DataSource.collection(collection))
                .execute()
                .allResults()
                .mapNotNull { result ->
                    val dict = result.getDictionary(collection.name)
                    dict?.let {
                        try {
                            Json.decodeFromString<AddressDto>(it.toJSON())
                        } catch (e: Exception) {
                            Log.e("AddressServiceImpl", "Error decoding AddressDto: ${e.message}")
                            null
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("AddressServiceImpl", "Failed to fetch all addresses: ${e.message}")
            emptyList()
        }
    }

    override fun fetchOne(id: String): AddressDto? {
        return try {
            val doc = collection.getDocument(id) ?: return null
            Json.decodeFromString<AddressDto>(doc.toJSON())
        } catch (e: Exception) {
            Log.e("AddressServiceImpl", "Failed to fetch address $id: ${e.message}")
            null
        }
    }

    override fun update(id: String, address: AddressDto): AddressDto {
        return try {
            val doc = collection.getDocument(id) ?: throw IllegalArgumentException("Address not found: $id")
            val updatedAddress = address.copy(id = id)
            val mutableDoc = doc.toMutable()
            mutableDoc.setJSON(Json.encodeToString(updatedAddress))
            collection.save(mutableDoc)
            updatedAddress
        } catch (e: Exception) {
            Log.e("AddressServiceImpl", "Failed to update address: ${e.message}")
            throw e
        }
    }
}
