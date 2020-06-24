package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import org.kindone.proptest.generator.Util

class ArbitraryKotlinInt : Generator<Int>() {
    override operator fun invoke(random: Random): Shrinkable<Int> {
        return Util.generateInteger(random)
    }
}