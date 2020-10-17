package insulator.di

import insulator.lib.configuration.model.Cluster
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.get
import org.koin.ext.scope
import tornadofx.DIContainer
import java.io.Closeable
import kotlin.reflect.KClass

class DIContainer : KoinComponent, DIContainer {
    init {
        startKoin { modules(kafkaModule, libModule) }
    }

    private val closeableInstances = mutableListOf<Closeable>()

    override fun <T : Any> getInstance(type: KClass<T>) =
        with(get<Cluster>().scope.get<T>(type)) {
            if (this is Closeable) closeableInstances.add(this)
            this
        }

    fun close() {
        closeableInstances.forEach { it.close() }
        stopKoin()
    }
}
