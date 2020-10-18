package insulator.di.dagger

import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import javax.inject.Named

@Module
class RootModule {

    @Provides
    fun provideJson() = Json { }

    @Provides
    @Named("configurationPath")
    fun provideConfigurationPath() = "${System.getProperty("user.home")}/.insulator.config"
}