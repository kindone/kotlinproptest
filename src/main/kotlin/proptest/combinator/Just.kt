package proptest.combinator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable

class Just {
    companion object {
        inline operator fun <T> invoke(value:T): Generator<T> {
            return object : Generator<T>() {
                override fun invoke(random: Random): Shrinkable<T> {
                    return Shrinkable(value)
                }
            }
        }
    }
}