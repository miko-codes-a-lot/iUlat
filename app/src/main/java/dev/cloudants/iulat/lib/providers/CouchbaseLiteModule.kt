package dev.cloudants.iulat.lib.providers

import android.content.Context
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import com.couchbase.lite.Collection

@Module
@InstallIn(SingletonComponent::class)
object CouchbaseLiteModule {

    @Provides
    @Singleton
    fun provideCouchbaseLite(@ApplicationContext context: Context): Database {
        CouchbaseLite.init(context)
        val db = Database("iulat")
        db.createCollection("users")
        return db
    }

    @Provides
    @Singleton
    fun provideUserCollection(db: Database): Collection {
        return db.getCollection("users") ?: db.createCollection("users")
    }
}