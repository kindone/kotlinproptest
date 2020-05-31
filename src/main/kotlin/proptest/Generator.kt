package org.kindone.proptest

interface Generator<T> {
    operator fun invoke(random: Random):Shrinkable<T>
}
