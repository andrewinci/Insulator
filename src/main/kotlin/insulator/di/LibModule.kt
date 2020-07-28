package insulator.di

import com.google.gson.GsonBuilder
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import insulator.lib.jsonhelper.JsonFormatter
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.SchemaRegistry
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.koin.ext.scope

inline fun <reified T> Scope.clusterScopedGet(qualifier: Qualifier? = null) = this
    .getKoin().get<Cluster>().scope.get<T>(qualifier)

val libModule = module {

    // Configurations
    single { GsonBuilder().setPrettyPrinting().create() }
    single { ConfigurationRepo(get()) }

    factory { AdminApi(clusterScopedGet(), clusterScopedGet()) }
    factory { Consumer(clusterScopedGet()) }
    factory { SchemaRegistry(clusterScopedGet()) }

    single { JsonFormatter(get()) }
}
