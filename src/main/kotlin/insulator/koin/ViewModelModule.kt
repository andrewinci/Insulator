package insulator.koin

import insulator.viewmodel.ConfigurationsViewModel
import org.koin.dsl.module


val viewModelModule = module {
    single { ConfigurationsViewModel() }
}

