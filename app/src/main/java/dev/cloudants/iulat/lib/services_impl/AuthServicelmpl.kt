package dev.cloudants.iulat.lib.services_impl

import android.util.Log
import com.couchbase.lite.Collection
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Expression
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.services.AuthService
import dev.cloudants.iulat.lib.services.EmailService
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import org.mindrot.jbcrypt.BCrypt
import java.lang.IllegalStateException

@Singleton
class AuthServiceImpl @Inject constructor(
    private val db: Database,
    private val emailServiceImpl: EmailServiceImpl
) : AuthService {
    private val userCollection by lazy {
        db.getCollection("users")
            ?: throw IllegalStateException("Users collection not created")
    }

    override suspend fun login(email: String, password: String): UserDto? {
        return try {
            val query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(userCollection))
                .where(Expression.property("email").equalTo(Expression.string(email)))

            val result = query.execute().firstOrNull()

            if (result != null) {
                val userJson = result.getDictionary(userCollection.name)?.toJSON()
                val user = Json.decodeFromString<UserDto>(userJson!!)
                if (BCrypt.checkpw(password, user.password)) {
                    user
                } else {
                    Log.w("AuthServiceImpl", "  Incorrect password for ${user.email} ${user.password} ")
                    null
                }
            } else {
                Log.w("AuthServiceImpl", "  No user found for email: $email")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthServiceImpl", "  Login error: ${e.message}")
            null
        }
    }

    override suspend fun verifyResetToken(email: String, token: String): Boolean {
        Log.d("AuthServiceImpl", "Verifying token with server: $token")
        return emailServiceImpl.verifyResetToken(email, token)
    }

    override suspend fun requestOTP(email: String): Boolean {
        Log.d("AuthServiceImpl", "Requesting OTP from server for: $email")
        return emailServiceImpl.requestPasswordResetToken(email)
    }
}
