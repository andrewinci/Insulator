package insulator.koin

import com.google.gson.GsonBuilder
import insulator.configuration.ConfigurationRepo
import insulator.kafka.AdminApi
import insulator.kafka.Consumer
import insulator.viewmodel.AddClusterViewModel
import insulator.viewmodel.ConfigurationsViewModel
import insulator.viewmodel.ListTopicViewModel
import org.koin.dsl.module


val viewModelModule = module {

    // Kafka
    factory { AdminApi(get()) }
    factory { Consumer(get()) }

    // Configurations
    single { GsonBuilder().setPrettyPrinting().create() }
    single { ConfigurationRepo(get()) }
    single { ConfigurationsViewModel(get()) }
    factory { AddClusterViewModel(get()) }

    // Main
    single { ListTopicViewModel(get()) }

}