package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import proptest.ContainerGenerator
import proptest.shrinker.ShrinkableSet

class ArbitraryKotlinCollectionsSet<T>(val elemGen:Generator<T>) : ContainerGenerator<Set<T>>() {

    override operator fun invoke(random:Random):Shrinkable<Set<T>> {
        val size = random.interval(minSize, maxSize)
        val set = emptySet<Shrinkable<T>>().toMutableSet()
        while (set.size < size) {
            set.add(elemGen(random))
        }

        return ShrinkableSet(set, minSize)
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