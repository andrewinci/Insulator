package insulator.di.factories

interface Factory<K, V> {
    fun build(key: K): V
}

fun <K, V> cachedFactory(op: (K) -> V) = object : Factory<K, V> {
    private val cache = mutableMapOf<K, V>()
    override fun build(key: K): V = cache.getOrPut(key) { op(key) }
}
