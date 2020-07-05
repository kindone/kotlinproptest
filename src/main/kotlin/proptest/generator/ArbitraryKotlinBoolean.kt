package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import proptest.shrinker.ShrinkableBoolean

class ArbitraryKotlinBoolean : Generator<Boolean>() {
    override operator fun invoke(random: Random): Shrinkable<Boolean> {
        val value = random.nextBoolean()
        return ShrinkableBoolean(value)
    }
}