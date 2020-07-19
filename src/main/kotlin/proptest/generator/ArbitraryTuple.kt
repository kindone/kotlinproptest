package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import proptest.shrinker.ShrinkableTuple

class ArbitraryTuple(val gens:List<Generator<*>>) : Generator<List<*>>() {
    override operator fun invoke(random: Random): Shrinkable<List<*>> {
        val tuple = gens.map { gen ->
            gen(random)
        }
        return ShrinkableTuple(tuple)
    }
}