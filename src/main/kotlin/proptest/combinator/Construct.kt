package proptest.combinator

import org.kindone.proptest.Generator
import org.kindone.proptest.Property
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import org.kindone.proptest.generator.ArbitraryKotlinCollectionsList
import kotlin.reflect.full.createType

class Construct {
    companion object {
        inline operator fun <reified T> invoke():T {
            return T::class.java.getDeclaredConstructor().newInstance() as T
        }

        inline operator fun <reified T, reified T1:Any> invoke(t1Gen: Generator<T1>? = null):Generator<T> {

            val kt = T::class.createType()
            val kt1 = T1::class.createType()
            val generators = Property.prepareGenerators(listOf(kt1), listOf(t1Gen))

            return object: Generator<T>() {
                override fun invoke(random: Random): Shrinkable<T> {
                    return (generators[0] as Generator<T1>)(random).transform {
                        val arg1 = it
                        T::class.java.getDeclaredConstructor(T1::class.java).newInstance(arg1) as T
                    }

                }
            }
        }

        inline operator fun <reified T, reified T1:Any, reified T2:Any> invoke(t1:T1, t2:T2,
                                                    t1Gen: Generator<T1>? = null, t2Gen:Generator<T2>? = null):T {
            return T::class.java.getDeclaredConstructor(T1::class.java, T2::class.java).newInstance(t1, t2) as T
        }
    }
}