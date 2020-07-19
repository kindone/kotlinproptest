package proptest.shrinker

import org.kindone.proptest.Shrinkable

fun <A,B> ShrinkablePair(pair:Pair<Shrinkable<A>, Shrinkable<B>>) : Shrinkable<Pair<A, B>> {
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