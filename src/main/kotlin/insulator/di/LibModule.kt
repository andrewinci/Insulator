package insulator.di

import com.google.gson.GsonBuilder
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.SchemaRegistry
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent


inline fun <reified T> clusterScopedGet(qualifier: Qualifier? = null) = KoinJavaComponent
        .getKoin()
        .getOrCreateScope(GlobalConfiguration.currentCluster.guid.toString(), named("clusterScope"))
        .get<T>(qualifier)

val libModule = module {

    // Configurations
    single { GsonBuilder().setPrettyPrinting().create() }
    single { ConfigurationRepo(get()) }

    factory { AdminApi(clusterScopedGet(), clusterScopedGet()) }
    factory { Consumer() }
    factory { SchemaRegistry(clusterScopedGet()) }
}