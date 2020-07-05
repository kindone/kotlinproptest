package proptest.shrinker

import org.kindone.proptest.Shrinkable

fun ShrinkableBoolean(value:Boolean):Shrinkable<Boolean> {
    // shrink true -> false
    if(value) {
        val seq = listOf(Shrinkable<Boolean>(false)).asSequence()
        return Shrinkable(value).with { -> seq }
    }
    else
        return Shrinkable(value)
}