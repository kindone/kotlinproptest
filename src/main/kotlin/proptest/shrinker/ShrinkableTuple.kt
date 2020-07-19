package proptest.shrinker

import org.kindone.proptest.Shrinkable

fun ShrinkableTuple(tuple:List<Shrinkable<*>>) : Shrinkable<List<*>> {
    var shr = Shrinkable(tuple)
    for(i in (0 until tuple.size)) {
        shr = shr.concat { parent ->
            parent.value[i].shrinks().map {
                val copy = parent.value.toMutableList()
                copy[i] = Shrinkable(it)
                Shrinkable(copy.toList())
            }
        }
    }

    return shr.transform {
        it.map {
            it.value
        }
    }
}