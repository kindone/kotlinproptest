package org.kindone.proptest.generator

import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable

object Util {


    private fun genpos(min:Long, max:Long):Sequence<Shrinkable<Long>> {
        val mid:Long = min/2 + max/2 + (if (min % 2 != 0L && max % 2 != 0L) 1 else 0)
        if(min + 1 >= max)
            return emptySequence<Shrinkable<Long>>()
        else if(min + 2 >= max)
            return sequenceOf(Shrinkable<Long>(mid))
        else {
            return sequenceOf(Shrinkable(mid).with { genpos(min, mid) }).plus(genpos(mid, max))
        }
    }

    private fun genneg(min:Long, max:Long):Sequence<Shrinkable<Long>> {
        val mid:Long = min/2 + max/2 + (if (min % 2 != 0L && max % 2 != 0L) -1 else 0)
        if(min + 1 >= max)
            return emptySequence<Shrinkable<Long>>()
        else if(min + 2 >= max)
            return sequenceOf(Shrinkable<Long>(mid))
        else {
            return sequenceOf(Shrinkable(mid).with { genpos(mid, max) }).plus(genpos(min, mid))
        }
    }

    fun binarySearchShrinkable(value:Long):Shrinkable<Long> {
        return Shrinkable(value).with {
            if(value == 0L)
                emptySequence()
            else if(value > 0)
                sequenceOf(Shrinkable<Long>(0L)).plus(genpos(0, value))
            else
                sequenceOf(Shrinkable<Long>(0L)).plus(genneg(value, 0))
        }
    }

    fun <T:Any> exhaustive(shrinkable:Shrinkable<T>, level:Int = 0, print:Boolean = true) {
        if(print) {
            for(i in (0 until level)) {
                print("  ")
            }
            println("Shrinkable: $shrinkable")
        }

        val shrinks = shrinkable.shrinks()
        for(shr in shrinks) {
            exhaustive(shr, level + 1, print)
        }
    }

    fun <T> generateInteger(rand:Random, min:T, max:T) {

    }
}