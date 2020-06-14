package org.kindone.proptest

class ArbitraryKotlinInt : Generator<Int>() {
    override operator fun invoke(random:Random):Shrinkable<Int> {
        return Shrinkable<Int>(random.nextLong().toInt())
    }
}

class ArbitraryKotlinCollectionsList<T>(elemGen:Generator<T>) : Generator<List<T>>() {

    override operator fun invoke(random:Random):Shrinkable<List<T>> {
        return Shrinkable<List<T>>(emptyList<T>())
    }
}