package insulator.views.common

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

val Subject.scope
    get() = object : StringScope("subject-${this.name}") {}

val Cluster.scope
    get() = object : StringScope("cluster-${this.guid}") {}

val String.topicScope
    get() = object : StringScope("topic-$this") {}
