package proptest.shrinker

import org.kindone.proptest.Shrinkable
import org.kindone.proptest.generator.IntegralType

fun <T> ShrinkableList(list:List<Shrinkable<T>>, minSize:Int):Shrinkable<List<T>> {
    val size = list.size
    val rangeShrinkable = IntegralType.binarySearchShrinkable(size - minSize).transform { it + minSize }
    val shrinkableList = rangeShrinkable.transform { newSize ->
        if(newSize == 0)
            emptyList<Shrinkable<T>>()
        else {
            list.subList(0, newSize)
        }
    }
    // TODO: shrink elements
    // strip value
    val listShrinkable = shrinkableList.transform { list ->
        list.map { it.value }
    }
    return listShrinkable
}