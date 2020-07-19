package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import proptest.shrinker.ShrinkableBoolean

class ArbitraryKotlinPair<A,B>(val aGen:Generator<A>, val bGen:Generator<B>) : Generator<Pair<A,B>>() {
    override operator fun invoke(random: Random): Shrinkable<Pair<A,B>> {
        val pair = Pair(aGen(random), bGen(random))
        return ShrinkablePair(pair)
    }

    companion object {
        fun <A,B> ShrinkablePair(pair:Pair<Shrinkable<A>,Shrinkable<B>>) : Shrinkable<Pair<A,B>> {
            // expand A first
            val shrAB = pair.first.transform {
                Pair(it, pair.second)
            }
            // expand B
            return shrAB.concat {  parent ->
                parent.value.second.shrinks().map {
                    Shrinkable(Pair(parent.value.first, it))
                }
            }.transform {
                // convert it to correct form
                Pair(it.first, it.second.value)
            }
        }
    }
}