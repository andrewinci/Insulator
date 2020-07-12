package insulator.koin

import kafkaModule
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import tornadofx.DIContainer
import kotlin.reflect.KClass

class DIContainer : KoinComponent, DIContainer {
    init {
        startKoin { modules(kafkaModule, viewModelModule) }
    }

    override fun <T : Any> getInstance(type: KClass<T>) = getKoin().get<T>(type)
}