package insulator.di

import insulator.lib.configuration.model.Cluster
import kafkaModule
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.ext.scope
import tornadofx.DIContainer
import tornadofx.FX
import tornadofx.getInstance
import kotlin.reflect.KClass

class DIContainer : KoinComponent, DIContainer {
    init {
        startKoin { modules(kafkaModule, libModule) }
    }

    override fun <T : Any> getInstance(type: KClass<T>) = get<Cluster>().scope.get<T>(type)
}

inline fun <reified T : Any> getInstanceNow() = FX.dicontainer!!.getInstance<T>()
