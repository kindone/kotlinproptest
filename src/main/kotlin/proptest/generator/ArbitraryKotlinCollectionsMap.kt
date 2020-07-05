package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import proptest.ContainerGenerator
import proptest.shrinker.ShrinkableMap
import proptest.shrinker.ShrinkableSet

class ArbitraryKotlinCollectionsMap<K,V>(val keyGen:Generator<K>, val valueGen:Generator<V>) : ContainerGenerator<Map<K,V>>() {

    override operator fun invoke(random:Random):Shrinkable<Map<K,V>> {
        val size = random.fromTo(minSize, maxSize)
        val map = emptyMap<Shrinkable<K>, Shrinkable<V>>().toMutableMap()
        while (map.size < size) {
            val key = keyGen(random)
            val value = valueGen(random)
            map[key] = value
        }

        return ShrinkableMap(map, minSize)
    }

    override fun setSize(min: Int, max: Int) {
        minSize = min
        maxSize = max
    }

    private var minSize = defaultMinSize
    private var maxSize = defaultMaxSize

    companion object {
        var defaultMinSize = 0
        var defaultMaxSize = 200
    }
}