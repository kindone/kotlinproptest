package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import proptest.type.Nullable
import proptest.shrinker.ShrinkableBoolean
import proptest.type.NotNull
import proptest.type.Null

class ArbitraryOrgKindonePropTestTypeNullable<T:Any>(val elemGen:Generator<T>) : Generator<T?>() {
    override operator fun invoke(random: Random): Shrinkable<T?> {
        val isNotNull = random.nextBoolean()
        if(isNotNull) {
            return elemGen(random).transform {
                it
            }
        }
        else {
            return Shrinkable<T?>(null)
        }
    }
}