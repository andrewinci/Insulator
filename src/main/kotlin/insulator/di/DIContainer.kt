package insulator.di

import kafkaModule
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import tornadofx.DIContainer
import tornadofx.FX
import tornadofx.getInstance
import kotlin.reflect.KClass

class DIContainer : KoinComponent, DIContainer {
    init {
        startKoin { modules(kafkaModule, libModule) }
    }

    override fun <T : Any> getInstance(type: KClass<T>) = getKoin().get<T>(type)
}

inline fun <reified T : Any> getInstanceNow() = FX.dicontainer!!.getInstance<T>()
