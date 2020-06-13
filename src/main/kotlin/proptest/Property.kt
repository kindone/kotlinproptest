package org.kindone.proptest

import java.lang.RuntimeException
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class Property(val scenario:Function<Unit>, val generators:List<Generator<*>>, val invoker:(Function<Unit>, List<Shrinkable<*>>) -> Unit) {

    private var seed:Long = 0

    init {
    }

    fun runner(rand:Random) {
        lateinit var savedRandom:Random
        try {
            for(i in 0..numRuns) {
                savedRandom = rand.clone()
                val shrinkables = generators.map {
                    it(rand)
                }
                invoker(scenario, shrinkables)
            }
        }
        catch(e:RuntimeException) {
            shrink(savedRandom)
        }
    }

    fun shrink(savedRandom:Random) {
        // TODO
    }

    fun forAll() {
        val random = org.kindone.proptest.Random(seed)
        runner(random)
    }

    fun setSeed(seed:Long) :Unit {
        this.seed = seed
    }

    override fun toString():String {
        return "Property"
    }

    companion object {
        val numRuns = 10

        inline operator fun <reified T1:Any> invoke(noinline f:(T1) -> Unit,
                                           t1Gen:Generator<T1>? = null):Property {
            val generators = prepareGenerators(listOf<KClass<*>>(T1::class), listOf<Generator<*>?>(t1Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1)
            }
            return Property(f, generators, invoker)
        }

        inline operator fun <reified T1:Any, reified T2:Any> invoke(noinline f:(T1, T2) -> Unit,
                                                           t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null):Property {
            val generators = prepareGenerators(listOf<KClass<*>>(T1::class, T2::class), listOf<Generator<*>?>(t1Gen, t2Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2)
            }
            return Property(f, generators, invoker)
        }

        inline operator fun <reified T1:Any, reified T2:Any, reified T3:Any> invoke(noinline f:(T1, T2, T3) -> Unit,
                                                                           t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null, t3Gen:Generator<T3>? = null):Property {
            val generators = prepareGenerators(listOf<KClass<*>>(T1::class, T2::class, T3::class), listOf<Generator<*>?>(t1Gen, t2Gen, t3Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2, shrinkables[2].value as T3)
            }
            return Property(f, generators, invoker)
        }

        inline operator fun <reified T1:Any, reified T2:Any, reified T3:Any, reified T4:Any> invoke(noinline f:(T1, T2, T3, T4) -> Unit,
                                                                                    t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null, t3Gen:Generator<T3>? = null, t4Gen:Generator<T4>? = null):Property {
            val generators = prepareGenerators(listOf<KClass<*>>(T1::class, T2::class, T3::class, T4::class), listOf<Generator<*>?>(t1Gen, t2Gen, t3Gen, t4Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2, shrinkables[2].value as T3, shrinkables[3].value as T4)
            }
            return Property(f, generators, invoker)
        }

        fun prepareGenerators(kclasses:List<KClass<*>>, explicitGens:List<Generator<*>?>):List<Generator<*>> {
            return kclasses.mapIndexed { index, kClass ->
                explicitGens[index] ?: getArbitraryOf(kClass)
            }
        }

        private fun toCamelcase(fqn:String):String {
            val camelcase = fqn.replace("""^[a-z]|\b""".toRegex()) {
                it.value.toUpperCase()
            }
            val dotRemoved = camelcase.replace(".", "")
            return dotRemoved
        }

        private fun <T:Any> getArbitraryOf(kc:KClass<T>):Generator<T> {
            val arbitraryName = "org.kindone.proptest.Arbitrary" + toCamelcase(kc.qualifiedName!!)

            try {
                val arbitraryKClass = Class.forName(arbitraryName).kotlin
                val arbitrary = arbitraryKClass.primaryConstructor!!.call() as Generator<T>
                println(arbitraryName + " found")
                return arbitrary
            }
            catch (e:ClassNotFoundException) {
                println("arbitrary for ${kc.qualifiedName} not found")
                throw e
            }
        }

    }
}