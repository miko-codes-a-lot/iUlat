package dev.cloudants.iulat.lib.providers

import android.content.Context
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.Database
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CouchbaseLiteModule {
    fun provideCouchbaseLite(
        @ApplicationContext context: Context,
    ): Database {
        CouchbaseLite.init(context)
        val db = Database("iulat")
        db.createCollection("users")
        return db
    }
}