package insulator.koin

import com.google.gson.GsonBuilder
import insulator.configuration.ConfigurationRepo
import insulator.viewmodel.AddClusterViewModel
import insulator.viewmodel.ConfigurationsViewModel
import org.koin.dsl.module


val viewModelModule = module {

    // Configurations
    single { GsonBuilder().setPrettyPrinting().create() }
    single { ConfigurationRepo(get()) }
    single { ConfigurationsViewModel(get()) }
    factory { AddClusterViewModel(get()) }

}

