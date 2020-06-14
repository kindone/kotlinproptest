package org.kindone.proptest

import java.lang.RuntimeException
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.reflect

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

//        inline fun <reified T:Any> printParamTypes() {
//            println("type parameters: " + T::class.java.)
//            println(getArbitraryOf<T>(T::class.qualifiedName!!))
//        }

        inline fun getParameterTypes(f:Function<Unit>):List<KType> {
            val kf = f.reflect()!!
            val ktypes = kf.parameters.map { param ->
                println("f param: " +param.type.classifier)
                for(typeargs in param.type.arguments) {
                    println("f typeargs: " + typeargs.type)

                }
                val ktype= param.type.classifier!!.createType(param.type.arguments.map { KTypeProjection.invariant(it.type!!) })
                println("reconstructed type: " + ktype)
                ktype
            }
            println("f return: " + kf.returnType)
            return ktypes
        }


        inline operator fun <reified T1:Any> invoke(noinline f:(T1) -> Unit,
                                           t1Gen:Generator<T1>? = null):Property {
            val generators = prepareGenerators(getParameterTypes(f), listOf<Generator<*>?>(t1Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1)
            }
            return Property(f, generators, invoker)
        }

        inline operator fun <reified T1:Any, reified T2:Any> invoke(noinline f:(T1, T2) -> Unit,
                                                           t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null):Property {
            val generators = prepareGenerators(getParameterTypes(f), listOf<Generator<*>?>(t1Gen, t2Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2)
            }
            return Property(f, generators, invoker)
        }

        inline operator fun <reified T1:Any, reified T2:Any, reified T3:Any> invoke(noinline f:(T1, T2, T3) -> Unit,
                                                                           t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null, t3Gen:Generator<T3>? = null):Property {
            val ktypes = getParameterTypes(f)
            val arbitraries = ktypes
            val generators = prepareGenerators(ktypes, listOf<Generator<*>?>(t1Gen, t2Gen, t3Gen))

            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2, shrinkables[2].value as T3)
            }
            return Property(f, generators, invoker)
        }

        inline operator fun <reified T1:Any, reified T2:Any, reified T3:Any, reified T4:Any> invoke(noinline f:(T1, T2, T3, T4) -> Unit,
                                                                                    t1Gen:Generator<T1>? = null, t2Gen:Generator<T2>? = null, t3Gen:Generator<T3>? = null, t4Gen:Generator<T4>? = null):Property {
            val generators = prepareGenerators(getParameterTypes(f), listOf<Generator<*>?>(t1Gen, t2Gen, t3Gen, t4Gen))
            val invoker = { f:Function<Unit>, shrinkables:List<Shrinkable<*>> ->
                f(shrinkables[0].value as T1, shrinkables[1].value as T2, shrinkables[2].value as T3, shrinkables[3].value as T4)
            }
            return Property(f, generators, invoker)
        }

//        fun prepareGenerators(kclasses:List<KClass<*>>, explicitGens:List<Generator<*>?>):List<Generator<*>> {
//            return kclasses.mapIndexed { index, kClass ->
//                explicitGens[index] ?: getArbitraryOf(kClass)
//            }
//        }

        fun prepareGenerators(ktypes:List<KType>, explicitGens:List<Generator<*>?>):List<Generator<*>> {
            return ktypes.mapIndexed { index, ktype ->
                val classifierName = ktype.classifier!!.toString()
                val className = classifierName.replace("class ".toRegex()) {
                    ""
                }
                println("class: " + className)
                for(arg in ktype.arguments) {
                    println("arg class: " + arg.type)
                    println("arg javaclass: " + arg.type!!.javaClass)
                }
                explicitGens[index] ?: getArbitraryOf(className)
            }
        }

        private fun toCamelcase(fqn:String):String {
            val camelcase = fqn.replace("""^[a-z]|(?<=[^A-Za-z])[a-z]""".toRegex()) {
                it.value.toUpperCase()
            }
            val dotRemoved = camelcase.replace(".", "")
            return dotRemoved
        }


        fun getArbitraryOf(qualifiedName:String):Generator<*> {
            val arbitraryName = "org.kindone.proptest.Arbitrary" + toCamelcase(qualifiedName)

            try {
//                Class.forName(arbitraryName).getDeclaredConstructor()
                val arbitraryKClass = Class.forName(arbitraryName).kotlin
//                println("java: " + arbitraryKClass.java)
//                println("javaO: " + arbitraryKClass.javaObjectType)
//                println("javaP: " + arbitraryKClass.javaPrimitiveType)
                try {
                    val arbitrary = arbitraryKClass.java.getDeclaredConstructor(Generator::class.java).newInstance()
                    return arbitrary
                }
                catch (e:NoSuchMethodException) {
                    println("method not found")
                }

                val arbitrary = arbitraryKClass.java.getDeclaredConstructor().newInstance() as Generator<*>// .primaryConstructor!!.call() as Generator<*>
                println(arbitraryName + " found")
//                return arbitraryKClass
                return arbitrary
            }
            catch (e:ClassNotFoundException) {
                println("arbitrary for ${qualifiedName} not found")
                throw e
            }
        }
    }
}