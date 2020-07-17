package insulator.lib.koin

import com.google.gson.GsonBuilder
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.Consumer
import org.koin.dsl.module


val libModule = module {

    // Kafka
    factory { AdminApi(get(), get()) }
    factory { Consumer(get()) }

    // Configurations
    single { GsonBuilder().setPrettyPrinting().create() }
    single { ConfigurationRepo(get()) }
}