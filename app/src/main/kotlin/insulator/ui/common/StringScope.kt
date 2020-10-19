package insulator.ui.common

import insulator.di.setGlobalCluster
import insulator.lib.configuration.model.Cluster
import insulator.lib.kafka.model.Subject
import tornadofx.FX
import tornadofx.Scope
import tornadofx.ScopedInstance
import java.io.Closeable

open class StringScope() : Closeable, Scope() {
    lateinit var value: String

    protected constructor(value: String) : this() {
        this.value = value
    }

    inline fun <reified T : ScopedInstance> withComponent(cmp: T): StringScope {
        FX.getComponents(this).getOrPut(T::class) { cmp }
        return this
    }

    override fun hashCode() = 5432 + value.hashCode()
    override fun equals(other: Any?) = (other as? StringScope)?.value == value
    override fun close() = super.deregister()
}

fun Subject.scope(cluster: Cluster) = object : StringScope("subject-${this.name}-${cluster.hashCode()}") {}

// The cluster scopes in tornado and in koin have to be the same
fun Cluster.scope() = (object : StringScope("cluster-${this.hashCode()}") {}).also { setGlobalCluster(this) }

fun String.topicScope(cluster: Cluster) = object : StringScope("topic-$this-${cluster.hashCode()}") {}
