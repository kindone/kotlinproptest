package org.kindone.proptest

class ArbitraryKotlinInt : Generator<Int>() {
    override operator fun invoke(random:Random):Shrinkable<Int> {

        return Shrinkable<Int>(random.nextLong().toInt())
    }
}

class ArbitraryKotlinCollectionsList<T>(val elemGen:Generator<T>) : Generator<List<T>>() {

    override operator fun invoke(random:Random):Shrinkable<List<T>> {
        val size = Math.abs(random.nextInt()) % 4
        val shrinkableList =
        (0 until size).map {
            elemGen(random)
        }
        val list = shrinkableList.map {
            it.value
        }
        return Shrinkable<List<T>>(list)
    }

    companion object {
        var defaultSize = 200
    }
}