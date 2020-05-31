package org.kindone.proptest

class Shrinkable<T>(val value:T) {
    fun shrinks():Sequence<Shrinkable<T>> {
        return sequenceOf() // TODO
    }
}