package insulator.di.components

import dagger.Component
import insulator.configuration.ConfigurationRepo
import insulator.di.modules.RootModule
import insulator.jsonhelper.JsonFormatter
import insulator.jsonhelper.avrotojson.AvroToJsonConverter
import insulator.jsonhelper.jsontoavro.JsonToAvroConverter
import insulator.ui.ThemeHelper
import insulator.views.configurations.ListClusterView
import javax.inject.Singleton

@Singleton
@Component(modules = [RootModule::class])
interface InsulatorComponent {
    fun getListClusterView(): ListClusterView
    fun getThemeHelper(): ThemeHelper

    // dependencies for downstream components
    fun configurationRepo(): ConfigurationRepo
    fun avroToJsonConverter(): AvroToJsonConverter
    fun jsonToAvroConverter(): JsonToAvroConverter
    fun jsonFormatter(): JsonFormatter
}
