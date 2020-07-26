package org.kindone.proptest

import kotlin.reflect.KType

abstract class Generator<T> {
    abstract operator fun invoke(random: Random):Shrinkable<T>

    fun <U> map(transformer: (T) -> U):Generator<U> {
        val self = this
        return object:Generator<U>() {
            override fun invoke(random: Random): Shrinkable<U> {
                return self.invoke(random).transform(transformer)
            }
        }
    }

    fun filter(filterer: (T) -> Boolean):Generator<T> {
        val self = this

        return object:Generator<T>() {
            override fun invoke(random: Random): Shrinkable<T> {
                return self.invoke(random).filter(filterer)
            }
        }
    }

    fun <U> chain(gen2gen:(T) -> Generator<U>):Generator<Pair<T, U>> {
        val self = this

        return object:Generator<Pair<T,U>>() {
            override fun invoke(random: Random): Shrinkable<Pair<T, U>> {
                val intermediate = self.invoke(random).transform {
                    Pair(it, gen2gen(it)(random))
                }
                return intermediate.andThen { shrinkable: Shrinkable<Pair<T, Shrinkable<U>>> ->
                    shrinkable.value.second.shrinks().map {
                        Shrinkable(Pair(shrinkable.value.first, it))
                    }
                }.transform {
                    Pair(it.first, it.second.value)
                }
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
