package proptest.combinator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import proptest.shrinker.ShrinkableTuple
import proptest.type.NotNull
import proptest.type.Null
import proptest.type.Nullable
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.starProjectedType

class Construct {
    companion object {
        inline operator fun <reified T> invoke():T {
            return T::class.java.getDeclaredConstructor().newInstance() as T
        }

        inline fun <reified T> createGenerator(generators:List<Generator<*>>, crossinline transformer:(List<Shrinkable<*>>) -> Shrinkable<T>):Generator<T> {
            return object: Generator<T>() {
                override fun invoke(random: Random): Shrinkable<T> {
                    val shrinkables = generators.map {
                        it(random)
                    }
                    return transformer(shrinkables)
                }
            }
        }

        inline fun <reified T> unbox(obj:Any):Any? {
            if(T::class == Nullable::class) {
                val nullable: Nullable<*> = obj as Nullable<*>
                if(nullable.isNull)
                    return null
                else
                    return (nullable as NotNull<*>).obj
            }
            if(T::class == Boolean::class) {
                val unboxed:Boolean? = (obj as Boolean)
                return unboxed
            }
            if(T::class == Byte::class) {
                val unboxed:Byte? = (obj as Byte)
                return unboxed
            }
            if(T::class == Char::class) {
                val unboxed:Char? = (obj as Char)
                return unboxed
            }
            if(T::class == Short::class) {
                val unboxed:Short? = (obj as Short)
                return unboxed
            }
            if(T::class == Int::class) {
                val unboxed:Int? = (obj as Int)
                return unboxed
            }
            if(T::class == Long::class) {
                val unboxed:Long? = (obj as Long)
                return unboxed
            }
            if(T::class == Float::class) {
                val unboxed:Float? = (obj as Float)
                return unboxed
            }
            if(T::class == Double::class) {
                val unboxed:Double? = (obj as Double)
                return unboxed
            }

            return obj
        }

        inline fun <reified T:Any?> isPrimitive():Boolean {
            if(T::class == Nullable::class)
                return false
            if(T::class == Boolean::class)
                return true
            if(T::class == Byte::class)
                return true
            if(T::class == Char::class)
                return true
            if(T::class == Short::class)
                return true
            if(T::class == Int::class)
                return true
            if(T::class == Long::class)
                return true
            if(T::class == Float::class)
                return true
            if(T::class == Double::class)
                return true

            return false
        }

        inline fun <reified T : Any?> javaTypeOf(obj:Any? = null):Class<*>? {
            println("javaTypeOf: " + T::class)
            if(T::class == Nullable::class) {
                val nullable = (obj!! as Nullable<*>)
                return if(nullable.isNull) KType::class.java else T::class.java
            }
            else if(isPrimitive<T>()) {
                val kc = T::class as KClass<*>
                return kc.javaPrimitiveType
            }
            else
                return T::class.java
        }

        inline fun <reified T : Any?> asJavaType(obj:Any):Any? {
            return if(isPrimitive<T>()) unbox<T>(obj) else obj
        }

        inline operator fun <reified T, reified T1:Any?> invoke(t1Gen: Generator<T1>? = null):Generator<T> {
            val gens = Generator.prepare(listOf(T1::class.createType(T1::class.starProjectedType.arguments)), listOf(t1Gen))
            val transformer = { shrinkables:List<Shrinkable<*>> ->
                ShrinkableTuple(shrinkables).transform {
                    T::class.java.getDeclaredConstructor(javaTypeOf<T1>(it[0]!!)).newInstance(asJavaType<T1>(it[0]!!)) as T
                }
            }
            return createGenerator(gens, transformer)
        }

        inline operator fun <reified T, reified T1:Any, reified T2:Any> invoke(t1Gen: Generator<T1>? = null, t2Gen:Generator<T2>? = null):Generator<T> {
            val gens = Generator.prepare(listOf(T1::class.createType(), T2::class.createType()), listOf(t1Gen, t2Gen))
            val transformer = { shrinkables:List<Shrinkable<*>> ->
                ShrinkableTuple(shrinkables).transform {
                    T::class.java.getDeclaredConstructor(javaTypeOf<T1>(), javaTypeOf<T2>()).newInstance(asJavaType<T1>(it[0]!!), asJavaType<T2>(it[1]!!)) as T
                }
            }
            return createGenerator(gens, transformer)
        }
    }
}