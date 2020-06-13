package org.kindone.proptest

class Shrinkable<T>(val value:T, val shrinksGen:() -> Sequence<Shrinkable<T>>) {
    constructor(value:T) : this(value, { -> emptySequence<Shrinkable<T>>() })

    fun shrinks():Sequence<Shrinkable<T>> = shrinksGen()

    fun with(shrinksGen:() -> Sequence<Shrinkable<T>>):Shrinkable<T> {
        return Shrinkable(value, shrinksGen)
    }

    fun<U> transform(transformer:(T) -> U):Shrinkable<U> {
        val shrinkable:Shrinkable<U> = Shrinkable<U>(transformer(value))
        return shrinkable.with({
            shrinksGen().map {shr ->
                shr.transform<U>(transformer)
            }
        })
    }

    fun filter(criteria:(T) -> Boolean):Shrinkable<T> {
        return with({
            shrinksGen().filter { shr ->
                criteria(shr.value)
            }
        })
    }

    fun take(n:Int):Shrinkable<T> {
        return with({
            shrinksGen().take(n)
        })
    }

    fun concat(then: () -> Sequence<Shrinkable<T>>):Shrinkable<T> {
        return with({
            shrinksGen().plus(then())
        })
    }

    fun concat(then: (Shrinkable<T>) -> Sequence<Shrinkable<T>>):Shrinkable<T> {
        val self = this
        return with({
            shrinksGen().map { shr ->
                shr.concat(then)
            }.plus(then(self))
        })
    }

    fun andThen(then: () -> Sequence<Shrinkable<T>>):Shrinkable<T> {
        return with({
            shrinksGen().map { shr ->
                shr.with({shr.shrinks().map {
                  it.andThen(then)
                }.ifEmpty(then)})
            }
        })
    }

    fun andThen(then: (Shrinkable<T>) -> Sequence<Shrinkable<T>>):Shrinkable<T> {
        return with({
            shrinksGen().map { shr ->
                shr.with({shr.shrinks().map {
                    it.andThen(then)
                }.ifEmpty({ -> then(shr)})})
            }
        })
    }
}