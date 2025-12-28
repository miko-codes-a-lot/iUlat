package dev.cloudants.iulat.lib.providers

import android.content.Context
import android.util.Log
import com.couchbase.lite.BasicAuthenticator
import com.couchbase.lite.CollectionConfiguration
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.Database
import com.couchbase.lite.Replicator
import com.couchbase.lite.ReplicatorConfigurationFactory
import com.couchbase.lite.ReplicatorType
import com.couchbase.lite.URLEndpoint
import com.couchbase.lite.newConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.cloudants.iulat.BuildConfig
import jakarta.inject.Singleton
import java.net.URI

@Module
@InstallIn(SingletonComponent::class)
object CouchbaseLiteModule {

    @Provides
    @Singleton
    fun provideCouchbaseLite(@ApplicationContext context: Context): Database {
        CouchbaseLite.init(context)
        val db = Database("iulat")
        val userCollection = db.createCollection("users")
        val chatCollection = db.createCollection("chats")
        val messageCollection = db.createCollection("messages")
        val addressCollection = db.createCollection("address")
        val garbageDisposalCollection = db.createCollection("garbage_disposal")
        val brokenStreetLightsCollection = db.createCollection("broken_streetlights")
        val noWaterSupplyCollection = db.createCollection("no_water_supply")
        val othersCollection = db.createCollection("others")
        val publicDisturbanceCollection = db.createCollection("public_disturbance")
        val roadRepairCollection = db.createCollection("road_repair")
        val robberiesCollection = db.createCollection("robberies")
        val vehicleCrashCollection = db.createCollection("vehicle_crash")
        val timelineEventsCollection = db.createCollection("timeline_events")
        val notificationsCollection = db.createCollection("notifications")

        val collections = mutableListOf(
            userCollection,
            chatCollection,
            messageCollection,
            addressCollection,
            garbageDisposalCollection,
            brokenStreetLightsCollection,
            noWaterSupplyCollection,
            othersCollection,
            publicDisturbanceCollection,
            roadRepairCollection,
            robberiesCollection,
            vehicleCrashCollection,
            timelineEventsCollection,
            notificationsCollection
        )

        val repl = Replicator(
            ReplicatorConfigurationFactory.newConfig(
                collections = CollectionConfiguration.fromCollections(collections),
                target = URLEndpoint(URI(BuildConfig.COUCHBASE_APP_SERVICE_URI)),
                type = ReplicatorType.PUSH_AND_PULL,
                continuous = true,
                authenticator = BasicAuthenticator(
                    BuildConfig.COUCHBASE_BASIC_USER,
                    BuildConfig.COUCHBASE_BASIC_PWD.toCharArray(),
                )
            )
        )

        val token = repl.addChangeListener { change ->
            val err: CouchbaseLiteException? = change.status.error
            if (err != null) {
                Log.d("micool", "Error code ::  ${err.code}")
            }
            Log.d("micool", change.status.toString())
        }

        repl.start()


        return db
    }
}
