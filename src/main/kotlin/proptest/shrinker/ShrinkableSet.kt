package proptest.shrinker

import org.kindone.proptest.Shrinkable
import org.kindone.proptest.generator.IntegralType

fun <T> ShrinkableSet(set:Set<Shrinkable<T>>, minSize:Int):Shrinkable<Set<T>> {
    val size = set.size
    val rangeShrinkable = IntegralType.binarySearchShrinkable(size - minSize).transform { it + minSize }
    val shrinkableList = rangeShrinkable.transform { newSize ->
        if(newSize == 0)
            emptyList<Shrinkable<T>>()
        else {
            set.toList().subList(0, newSize)
        }
    }
    // TODO: shrink elements
    // strip value
    val shrinkableSet = shrinkableList.transform { list ->
        list.map { it.value }.toSet()
    }
    return shrinkableSet
}