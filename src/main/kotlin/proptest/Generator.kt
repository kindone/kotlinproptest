package org.kindone.proptest

import kotlin.reflect.KType

abstract class Generator<T> {
    abstract operator fun invoke(random: Random):Shrinkable<T>

    fun <U> transform(transformer: (T) -> U):Generator<U> {
        val self = this
        return object:Generator<U>() {
            override fun invoke(random: Random): Shrinkable<U> {
                return self.invoke(random).transform(transformer)
            }
        }
    }

    companion object {
        fun prepare(ktypes:List<KType>, explicitGens:List<Generator<*>?>):List<Generator<*>> {
            return ktypes.mapIndexed { index, ktype ->
                explicitGens[index] ?: Property.getArbitraryOf(ktype)
            }
        }
    }
}
