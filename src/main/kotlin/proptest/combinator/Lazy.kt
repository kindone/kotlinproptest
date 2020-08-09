package proptest.combinator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable

class Lazy {
    companion object {
        inline operator fun <T> invoke(crossinline lazy: () ->  T): Generator<T> {
            return object : Generator<T>() {
                override fun invoke(random: Random): Shrinkable<T> {
                    return Shrinkable<T>(lazy())
                }
            }
        }
    }
}