package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import proptest.shrinker.ShrinkableDouble

class ArbitraryKotlinDouble : Generator<Double>() {
    override operator fun invoke(random: Random): Shrinkable<Double> {
        val value = random.nextDouble()
        return ShrinkableDouble(value)
    }
}