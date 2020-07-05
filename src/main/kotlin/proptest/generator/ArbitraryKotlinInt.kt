package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable

class ArbitraryKotlinInt : Generator<Int>() {
    override operator fun invoke(random: Random): Shrinkable<Int> {
        return IntegralType.generateInteger(random)
    }
}