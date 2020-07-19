package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import proptest.shrinker.ShrinkableTriple

class ArbitraryKotlinTriple<A,B,C>(val aGen:Generator<A>, val bGen:Generator<B>, val cGen:Generator<C>) : Generator<Triple<A,B,C>>() {
    override operator fun invoke(random: Random): Shrinkable<Triple<A,B,C>> {
        val triple = Triple(aGen(random), bGen(random), cGen(random))
        return ShrinkableTriple(triple)
    }
}