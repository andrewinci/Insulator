package insulator

abstract class CachedFactory<K, V>(private val op: (K) -> V) {
    private val cache = mutableMapOf<K, V>()
    fun build(key: K): V = cache.getOrPut(key) { op(key) }
}
