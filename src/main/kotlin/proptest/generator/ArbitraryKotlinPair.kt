package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import proptest.shrinker.ShrinkableBoolean
import proptest.shrinker.ShrinkablePair

class ArbitraryKotlinPair<A,B>(val aGen:Generator<A>, val bGen:Generator<B>) : Generator<Pair<A,B>>() {
    override operator fun invoke(random: Random): Shrinkable<Pair<A,B>> {
        val pair = Pair(aGen(random), bGen(random))
        return ShrinkablePair(pair)
    }
}