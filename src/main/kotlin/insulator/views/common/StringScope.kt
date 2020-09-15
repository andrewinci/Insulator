package insulator.views.common

import tornadofx.FX
import tornadofx.Scope
import tornadofx.ScopedInstance
import java.io.Closeable

class StringScope(val value: String) : Closeable, Scope() {

    inline fun <reified T : ScopedInstance> withComponent(cmp: T): StringScope {
        FX.getComponents(this).getOrPut(T::class) { cmp }
        return this
    }

    override fun hashCode() = 5432 + value.hashCode()
    override fun equals(other: Any?) = (other as? StringScope)?.value == value
    override fun close() = super.deregister()
}
