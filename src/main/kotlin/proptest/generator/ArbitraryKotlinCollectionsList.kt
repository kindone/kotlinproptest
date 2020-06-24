package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import proptest.ContainerGenerator

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