package proptest

import org.kindone.proptest.Generator

abstract class ContainerGenerator<T> : Generator<T>() {
    abstract fun setSize(min:Int, max:Int = min)
}