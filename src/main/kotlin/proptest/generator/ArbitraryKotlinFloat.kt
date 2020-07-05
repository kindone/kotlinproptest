package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import proptest.shrinker.ShrinkableDouble

class ArbitraryKotlinFloat : Generator<Float>() {
    override operator fun invoke(random: Random): Shrinkable<Float> {
        val value = random.nextDouble()
        return ShrinkableDouble(value).transform { it.toFloat() }
    }
}