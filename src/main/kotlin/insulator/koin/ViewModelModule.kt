package insulator.koin

import insulator.configuration.ConfigurationRepo
import insulator.viewmodel.AddClusterViewModel
import insulator.viewmodel.ConfigurationsViewModel
import org.koin.dsl.module


val viewModelModule = module {

    // Configurations
    single { ConfigurationRepo() }
    single { ConfigurationsViewModel(get()) }
    factory { AddClusterViewModel(get()) }

}

