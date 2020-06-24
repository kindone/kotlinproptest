package org.kindone.proptest

import org.kindone.proptest.generator.Util
import proptest.ContainerGenerator

class ArbitraryKotlinInt : Generator<Int>() {
    override operator fun invoke(random:Random):Shrinkable<Int> {
        return Util.generateInteger(random)
    }
}

class ArbitraryKotlinLong : Generator<Long>() {
    override operator fun invoke(random:Random):Shrinkable<Long> {
        return Util.generateLong(random)
    }
}

class ArbitraryKotlinCollectionsList<T>(val elemGen:Generator<T>) : ContainerGenerator<List<T>>() {

    override operator fun invoke(random:Random):Shrinkable<List<T>> {
        val size = random.fromTo(minSize, maxSize)
        val list = (0 until size).map {
            elemGen(random)
        }
        val rangeShrinkable = Util.binarySearchShrinkable(size - minSize).transform { it + minSize }
        val shrinkableList = rangeShrinkable.transform { newSize ->
            if(newSize == 0)
                emptyList<Shrinkable<T>>()
            else {
                list.subList(0, newSize)
            }
        }
        // TODO: shrink elements
        // strip value
        val listShrinkable = shrinkableList.transform { list ->
            list.map { it.value }
        }
        return listShrinkable
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