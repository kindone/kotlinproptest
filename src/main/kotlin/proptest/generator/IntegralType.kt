package org.kindone.proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable

object IntegralType {


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

    fun binarySearchShrinkable(value:Int):Shrinkable<Int> {
        return binarySearchShrinkable(value.toLong()).transform { it.toInt() }
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

    fun generateInteger(rand:Random, min:Int = Int.MIN_VALUE, max:Int = Int.MAX_VALUE):Shrinkable<Int> {
        var value =
            if(min == Int.MIN_VALUE && max == Int.MAX_VALUE && rand.nextBoolean()) {
                if(rand.nextBoolean()) Int.MIN_VALUE else Int.MAX_VALUE
            } else if(min == Int.MIN_VALUE && max == Int.MAX_VALUE){
                rand.nextInt()
            }
            else
                rand.fromTo(min, max)

        if(value < min || max < value)
            throw RuntimeException("invalid range")

        if(min >= 0)
            return binarySearchShrinkable(value - min).transform { it + min }
        else if(max <= 0)
            return binarySearchShrinkable(value - max).transform { it + max }
        else
            return binarySearchShrinkable(value)
    }

    fun generateLong(rand:Random, min:Long = Long.MIN_VALUE, max:Long = Long.MAX_VALUE):Shrinkable<Long> {
        var value = 0L
        if(min == Long.MIN_VALUE && max == Long.MAX_VALUE && rand.nextBoolean()) {
            value = if(rand.nextBoolean()) Long.MIN_VALUE else Long.MAX_VALUE
        } else if(min == Long.MIN_VALUE && max == Long.MAX_VALUE){
            value = rand.nextLong()
        }
        else
            value = rand.fromTo(min, max)

        if(value < min || max < value)
            throw RuntimeException("invalid range")

        if(min >= 0)
            return binarySearchShrinkable(value - min).transform { value -> value + min }
        else if(max <= 0)
            return binarySearchShrinkable(value - max).transform { value -> value + max }
        else
            return binarySearchShrinkable(value)
    }

    fun fromTo(min:Int, max:Int):Generator<Int> {
        return object: Generator<Int>() {
            override fun invoke(random: Random): Shrinkable<Int> {
                return generateInteger(random, min, max)
            }
        }
    }

    fun fromTo(min:Long, max:Long):Generator<Long> {
        return object: Generator<Long>() {
            override fun invoke(random: Random): Shrinkable<Long> {
                return generateLong(random, min, max)
            }
        }
    }
}