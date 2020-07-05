package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable

class ArbitraryKotlinLong : Generator<Long>() {
    override operator fun invoke(random: Random): Shrinkable<Long> {
        return IntegralType.generateLong(random)
    }
}