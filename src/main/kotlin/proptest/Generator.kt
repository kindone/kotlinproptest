package org.kindone.proptest

abstract class Generator<T> {
    abstract operator fun invoke(random: Random):Shrinkable<T>
}
