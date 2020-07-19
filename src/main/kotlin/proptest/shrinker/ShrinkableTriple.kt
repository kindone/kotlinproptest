package proptest.shrinker

import org.kindone.proptest.Shrinkable

fun <A,B,C> ShrinkableTriple(triple:Triple<Shrinkable<A>, Shrinkable<B>, Shrinkable<C>>) : Shrinkable<Triple<A, B, C>> {
    // expand A first
    val shrABC = triple.first.transform {
        Triple(it, triple.second, triple.third)
    }
    // expand B
    return shrABC.concat {  parent ->
        parent.value.second.shrinks().map {
            Shrinkable(Triple(parent.value.first, it, parent.value.third))
        }
    }.concat { parent ->
        // expand C
        parent.value.third.shrinks().map {
            Shrinkable(Triple(parent.value.first, parent.value.second, it))
        }
    }.transform {
        // convert it to correct form
        Triple(it.first, it.second.value, it.third.value)
    }
}