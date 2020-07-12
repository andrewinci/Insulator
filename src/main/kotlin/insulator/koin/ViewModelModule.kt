package insulator.koin

import com.google.gson.GsonBuilder
import insulator.configuration.ConfigurationRepo
import insulator.kafka.AdminApi
import insulator.viewmodel.AddClusterViewModel
import insulator.viewmodel.ConfigurationsViewModel
import insulator.viewmodel.TopicsViewModel
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.common.config.SslConfigs
import org.koin.dsl.module
import java.util.*


val viewModelModule = module {

    // Kafka
    factory { AdminApi(get()) }

    // Configurations
    single { GsonBuilder().setPrettyPrinting().create() }
    single { ConfigurationRepo(get()) }
    single { ConfigurationsViewModel(get()) }
    factory { AddClusterViewModel(get()) }

    // Main
    single { TopicsViewModel(get()) }

}