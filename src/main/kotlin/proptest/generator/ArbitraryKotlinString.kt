package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import proptest.ContainerGenerator
import proptest.generator.StringType
import proptest.shrinker.ShrinkableList

class ArbitraryKotlinString(val elemGen:Generator<Char> = StringType.genASCII()) : ContainerGenerator<String>() {

    override operator fun invoke(random:Random):Shrinkable<String> {
        val size = random.interval(minSize, maxSize)
        val list = (0 until size).map {
            elemGen(random)
        }
        return ShrinkableList<Char>(list, minSize).transform { String(it.toCharArray()) }
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