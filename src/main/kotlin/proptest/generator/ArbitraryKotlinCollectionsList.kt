package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import proptest.ContainerGenerator
import proptest.shrinker.ShrinkableList

class ArbitraryKotlinCollectionsList<T>(val elemGen:Generator<T>) : ContainerGenerator<List<T>>() {

    override operator fun invoke(random:Random):Shrinkable<List<T>> {
        val size = random.interval(minSize, maxSize)
        val list = (0 until size).map {
            elemGen(random)
        }
        return ShrinkableList<T>(list, minSize)
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