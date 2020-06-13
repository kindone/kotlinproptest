package org.kindone.proptest

class ArbitraryKotlinInt : Generator<Int> {
    override operator fun invoke(random:Random):Shrinkable<Int> {
        return Shrinkable<Int>(random.nextLong().toInt())
    }
}