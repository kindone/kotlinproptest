package org.kindone.proptest

import proptest.AssertFailed
import java.lang.RuntimeException
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.reflect

class Property(val scenario:Function<Unit>, val generators:List<Generator<*>>, val invoker:(Function<Unit>, List<Shrinkable<*>>) -> Unit) {

    private var seed:Long = 0

    fun runner(rand:Random) {
        lateinit var savedRandom:Random
        var i = 0
        try {
            while(i < numRuns) {
                savedRandom = rand.clone()
                val shrinkables = generators.map {
                    it(rand)
                }
                invoker(scenario, shrinkables)
                i ++
            }
        }
        catch(e:AssertFailed) {
            println("Falsifiable, after " + (i + 1) + " tests - assertion failed: " + e.message)
            shrink(savedRandom)
        }
        catch(e:RuntimeException) {
            println("Falsifiable, after " + (i + 1) + " tests - unhandled exception thrown: " + e.message)
            shrink(savedRandom)
        }
        println("OK, passed " + numRuns + " tests")
    }

    private fun testN(shrinkables:List<Shrinkable<*>>, n:Int, replacement: Shrinkable<*>):Boolean {
        try {
            val replacedShrinkables = shrinkables.toMutableList()
            replacedShrinkables[n] = replacement
            invoker(scenario, replacedShrinkables)
        }
        catch(e:AssertFailed) {
            return false
        }
        catch(e:RuntimeException) {
            return false
        }
        return true
    }

    private fun shrinkN(shrinkables:MutableList<Shrinkable<*>>, N:Int) {
        var shrinks = shrinkables[N].shrinks()
        while(!shrinks.none()) {
            var shrinkFound = false
            // if any of shrinkable fails test again, go deeper down
            // otherwise, stop
            for(shrinkable in shrinks) {
                if(!testN(shrinkables, N, shrinkable)) {
                    shrinks = shrinkable.shrinks()
                    shrinkables[N] = shrinkable
                    shrinkFound = true
                    break
                }
            }
            if(shrinkFound)
            {
                println("  shrinking found simpler failing arg " + N + ": " + shrinkables)
            }
            else
                break
        }
    }

    private fun shrink(savedRandom:Random) {
        val shrinkables = generators.map {
            it(savedRandom)
        }.toMutableList()

        for(i in (0 until shrinkables.size)) {
            shrinkN(shrinkables, i)
        }
        println("  simplest args found by shrinking: " + shrinkables)
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

        // 1 arg
        inline operator fun <reified T1:Any> invoke(noinline f:(T1) -> Unit,
                                           t1Gen:Generator<T1>? = null):Property {
            val generators = Generator.prepare(getParameterTypes(f), listOf<Generator<*>?>(t1Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1)
            }
            return Property(f, generators, invoker)
        }

        // 2 args
        inline operator fun <reified T1:Any, reified T2:Any> invoke(noinline f:(T1, T2) -> Unit,
                                                           t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null):Property {
            val generators = Generator.prepare(getParameterTypes(f), listOf<Generator<*>?>(t1Gen, t2Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2)
            }
            return Property(f, generators, invoker)
        }

        // 3 args
        inline operator fun <reified T1:Any, reified T2:Any, reified T3:Any> invoke(noinline f:(T1, T2, T3) -> Unit,
                                                                           t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null, t3Gen:Generator<T3>? = null):Property {
            val generators = Generator.prepare(getParameterTypes(f), listOf<Generator<*>?>(t1Gen, t2Gen, t3Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2, shrinkables[2].value as T3)
            }
            return Property(f, generators, invoker)
        }

        // 4 args
        inline operator fun <reified T1:Any, reified T2:Any, reified T3:Any, reified T4:Any> invoke(noinline f:(T1, T2, T3, T4) -> Unit,
                                                                                    t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null, t3Gen:Generator<T3>? = null, t4Gen:Generator<T4>? = null):Property {
            val generators = Generator.prepare(getParameterTypes(f), listOf<Generator<*>?>(t1Gen, t2Gen, t3Gen, t4Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2, shrinkables[2].value as T3, shrinkables[3].value as T4)
            }
            return Property(f, generators, invoker)
        }

        // 5 args
        inline operator fun <reified T1:Any, reified T2:Any, reified T3:Any, reified T4:Any, reified T5:Any> invoke(noinline f:(T1, T2, T3, T4, T5) -> Unit,
                                                                                                    t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null, t3Gen:Generator<T3>? = null, t4Gen:Generator<T4>? = null, t5Gen:Generator<T5>? = null):Property {
            val generators = Generator.prepare(getParameterTypes(f), listOf<Generator<*>?>(t1Gen, t2Gen, t3Gen, t4Gen, t5Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2, shrinkables[2].value as T3, shrinkables[3].value as T4, shrinkables[4].value as T5)
            }
            return Property(f, generators, invoker)
        }

        // 6 args
        inline operator fun <reified T1:Any, reified T2:Any, reified T3:Any, reified T4:Any, reified T5:Any, reified T6:Any> invoke(noinline f:(T1, T2, T3, T4, T5, T6) -> Unit,
                                                                                                                    t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null, t3Gen:Generator<T3>? = null, t4Gen:Generator<T4>? = null, t5Gen:Generator<T5>? = null, t6Gen:Generator<T6>? = null):Property {
            val generators = Generator.prepare(getParameterTypes(f), listOf<Generator<*>?>(t1Gen, t2Gen, t3Gen, t4Gen, t5Gen, t6Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2, shrinkables[2].value as T3, shrinkables[3].value as T4, shrinkables[4].value as T5, shrinkables[5].value as T6)
            }
            return Property(f, generators, invoker)
        }

        // 7 args
        inline operator fun <reified T1:Any, reified T2:Any, reified T3:Any, reified T4:Any, reified T5:Any, reified T6:Any, T7:Any> invoke(noinline f:(T1, T2, T3, T4, T5, T6, T7) -> Unit,
                                                                                                                                    t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null, t3Gen:Generator<T3>? = null, t4Gen:Generator<T4>? = null, t5Gen:Generator<T5>? = null, t6Gen:Generator<T6>? = null, t7Gen:Generator<T7>? = null):Property {
            val generators = Generator.prepare(getParameterTypes(f), listOf<Generator<*>?>(t1Gen, t2Gen, t3Gen, t4Gen, t5Gen, t6Gen, t7Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2, shrinkables[2].value as T3, shrinkables[3].value as T4, shrinkables[4].value as T5, shrinkables[5].value as T6, shrinkables[6].value as T7)
            }
            return Property(f, generators, invoker)
        }

        // 8 args
        inline operator fun <reified T1:Any, reified T2:Any, reified T3:Any, reified T4:Any, reified T5:Any, reified T6:Any, T7:Any, T8:Any> invoke(noinline f:(T1, T2, T3, T4, T5, T6, T7, T8) -> Unit,
                                                                                                                                            t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null, t3Gen:Generator<T3>? = null, t4Gen:Generator<T4>? = null, t5Gen:Generator<T5>? = null, t6Gen:Generator<T6>? = null, t7Gen:Generator<T7>? = null, t8Gen:Generator<T8>? = null):Property {
            val generators = Generator.prepare(getParameterTypes(f), listOf<Generator<*>?>(t1Gen, t2Gen, t3Gen, t4Gen, t5Gen, t6Gen, t7Gen, t8Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2, shrinkables[2].value as T3, shrinkables[3].value as T4, shrinkables[4].value as T5, shrinkables[5].value as T6, shrinkables[6].value as T7, shrinkables[7].value as T8)
            }
            return Property(f, generators, invoker)
        }

        fun getParameterTypes(f:Function<Unit>):List<KType> {
            val kf = f.reflect()!!
            val ktypes = kf.parameters.map { param ->
                val ktype= param.type//.classifier!!.createType(param.type.arguments/*.map { KTypeProjection.invariant(it.type!!) }*/)
                ktype
            }
            return ktypes
        }

        fun getClassName(classifier:KClassifier):String {
            val classifierName = classifier.toString()
            return classifierName.replace("class ".toRegex()) {
                ""
            }
        }

        fun getArbitraryName(qualifiedName:String):String {
            val camelcase = qualifiedName.replace("""^[a-z]|(?<=[^A-Za-z])[a-z]""".toRegex()) {
                it.value.toUpperCase()
            }
            val dotRemoved = camelcase.replace(".", "")
            return "org.kindone.proptest.generator.Arbitrary" + dotRemoved
        }

        fun getArbitraryOf(ktype:KType):Generator<*> {
            val className = getClassName(ktype.classifier!!)
            if(ktype.arguments.isEmpty()) {
                return getArbitraryOf(className)
            }
            else {
                val elemGens = ktype.arguments.map { arg ->
                    getArbitraryOf(arg.type!!)
                }
                return getArbitraryOf(className, elemGens)
            }
        }

        fun getArbitraryOf(qualifiedName:String, elemGens:List<Generator<*>> = emptyList()):Generator<*> {
            val arbitraryName = getArbitraryName(qualifiedName)
            try {
                val arbitraryKClass = Class.forName(arbitraryName).kotlin
                val geneneratorTypes = elemGens.map {
                    Generator::class.java
                }.toTypedArray()
                val arbitrary = arbitraryKClass.java.getDeclaredConstructor(*geneneratorTypes).newInstance(*elemGens.toTypedArray()) as Generator<*>
                println("arbitary for ${qualifiedName}:${elemGens.toString()} ${arbitraryName} found")
                return arbitrary
            } catch (e:ClassNotFoundException) {
                println("arbitrary for ${qualifiedName} ${arbitraryName} not found")
                throw e
            } catch (e:NoSuchMethodException) {
                println("constructor for ${qualifiedName}:${elemGens.toString()} not found")
                throw e
            }
        }
    }
}