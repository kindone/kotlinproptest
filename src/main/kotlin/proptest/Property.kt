package org.kindone.proptest

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

        fun getParameterTypes(f:Function<Unit>):List<KType> {
            val kf = f.reflect()!!
            val ktypes = kf.parameters.map { param ->
                val ktype= param.type.classifier!!.createType(param.type.arguments.map { KTypeProjection.invariant(it.type!!) })
                ktype
            }
//            println("f return: " + kf.returnType)
            return ktypes
        }

        inline operator fun <reified T1:Any> invoke(noinline f:(T1) -> Unit,
                                           t1Gen:Generator<T1>? = null):Property {
            val generators = Generator.prepare(getParameterTypes(f), listOf<Generator<*>?>(t1Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1)
            }
            return Property(f, generators, invoker)
        }

        inline operator fun <reified T1:Any, reified T2:Any> invoke(noinline f:(T1, T2) -> Unit,
                                                           t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null):Property {
            val generators = Generator.prepare(getParameterTypes(f), listOf<Generator<*>?>(t1Gen, t2Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2)
            }
            return Property(f, generators, invoker)
        }

        inline operator fun <reified T1:Any, reified T2:Any, reified T3:Any> invoke(noinline f:(T1, T2, T3) -> Unit,
                                                                           t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null, t3Gen:Generator<T3>? = null):Property {
            val generators = Generator.prepare(getParameterTypes(f), listOf<Generator<*>?>(t1Gen, t2Gen, t3Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2, shrinkables[2].value as T3)
            }
            return Property(f, generators, invoker)
        }

        inline operator fun <reified T1:Any, reified T2:Any, reified T3:Any, reified T4:Any> invoke(noinline f:(T1, T2, T3, T4) -> Unit,
                                                                                    t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null, t3Gen:Generator<T3>? = null, t4Gen:Generator<T4>? = null):Property {
            val generators = Generator.prepare(getParameterTypes(f), listOf<Generator<*>?>(t1Gen, t2Gen, t3Gen, t4Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2, shrinkables[2].value as T3, shrinkables[3].value as T4)
            }
            return Property(f, generators, invoker)
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