package proptest.shrinker

import org.kindone.proptest.Shrinkable
import org.kindone.proptest.generator.IntegralType

fun <K,V> ShrinkableMap(map:Map<Shrinkable<K>,Shrinkable<V>>, minSize:Int):Shrinkable<Map<K,V>> {
    val size = map.size
    val rangeShrinkable = IntegralType.binarySearchShrinkable(size - minSize).transform { it + minSize }
    val shrinkableList = rangeShrinkable.transform { newSize ->
        if(newSize == 0)
            emptyList<Pair<Shrinkable<K>,Shrinkable<V>>>()
        else {
            map.toList().subList(0, newSize)
        }
    }
    // TODO: shrink elements
    // strip value
    val shrinkableMap = shrinkableList.transform { list ->
        list.map {
                Pair(it.first.value, it.second.value)
        }.toMap()
    }
    return shrinkableMap
}