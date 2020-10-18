package insulator.di.dagger.components

import dagger.Component
import insulator.di.dagger.modules.RootModule
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.jsonhelper.JsonFormatter
import insulator.lib.jsonhelper.avrotojson.AvroToJsonConverter
import insulator.lib.jsonhelper.jsontoavro.JsonToAvroConverter
import insulator.views.configurations.ListClusterView
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Singleton
@Component(modules = [RootModule::class])
interface InsulatorComponent {
    fun getListClusterView(): ListClusterView

    // dependencies for downstream components
    fun json(): Json
    fun configurationRepo(): ConfigurationRepo
    fun avroToJsonConverter(): AvroToJsonConverter
    fun jsonToAvroConverter(): JsonToAvroConverter
    fun jsonFormatter(): JsonFormatter
}