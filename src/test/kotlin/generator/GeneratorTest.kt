package generator

import io.kotest.core.spec.style.StringSpec
import org.kindone.proptest.Random
import org.kindone.proptest.generator.ArbitraryKotlinCollectionsList
import org.kindone.proptest.generator.ArbitraryKotlinString
import org.kindone.proptest.generator.IntegralType
import proptest.combinator.Construct
import proptest.combinator.ElementOf
import proptest.combinator.Just
import proptest.combinator.OneOf
import kotlin.reflect.jvm.reflect

class GeneratorTest : StringSpec() {
    inline fun  <reified T1:Any, reified T2:Any> reflectFunc(noinline f:(T1, T2) -> Unit) {
        val kt = f.reflect()!!
        kt.parameters
    }

    init {

        "Construct1" {
            class SomeClass(val a:String) {

            }
            val stringGen = ArbitraryKotlinString()
            val someClassGen = Construct<SomeClass, String>(stringGen)
            val rand = Random()
            val shrinkable = someClassGen(rand)
            IntegralType.exhaustive(shrinkable)
        }

        "Construct1.primitive" {
            class SomeClass(val i:Int) {
                override fun toString(): String {
                    return "SomeClass($i)"
                }
            }
            val intGen = IntegralType.interval(0,10)
            val someClassGen = Construct<SomeClass, Int>(intGen)
            val rand = Random()
            val shrinkable = someClassGen(rand)
            IntegralType.exhaustive(shrinkable)
        }

        "Construct1.generic" {
            class SomeClass(val l:List<Int>) {
                override fun toString(): String {
                    return "SomeClass($l)"
                }
            }
            val intGen = IntegralType.interval(0,10)
            val intListGen = ArbitraryKotlinCollectionsList<Int>(intGen)
            val someClassGen = Construct<SomeClass, List<Int>>(intListGen)
            val rand = Random()
            val shrinkable = someClassGen(rand)
            IntegralType.exhaustive(shrinkable)
        }

        // FIXME: doesn't work
//        "Construct1.nullable" {
//            class SomeClass(val i:Int?) {
//                override fun toString(): String {
//                    return "SomeClass($i!!)"
//                }
//            }
//
//            val intGen = IntegralType.interval(0,10)
//            val nullableIntGen = ArbitraryOrgKindonePropTestTypeNullable(intGen)
//            val someClassGen = Construct<SomeClass, Int?>(nullableIntGen)
//            val rand = Random()
//            val shrinkable = someClassGen(rand)
//            IntegralType.exhaustive(shrinkable)
//        }

        "Construct2" {
            class SomeClass(val i:Int, val a:String) {
                override fun toString(): String {
                    return "SomeClass($i, \"$a\")"
                }
            }
            val intGen = IntegralType.interval(0,10)
            val stringGen = ArbitraryKotlinString()
            val someClassGen = Construct<SomeClass, Int, String>(intGen, stringGen)
            val rand = Random()
            val shrinkable = someClassGen(rand)
            IntegralType.exhaustive(shrinkable)
        }

        "Just" {
            val gen = Just(3)
            val rand = Random()
            for(i in 0..2)
                IntegralType.exhaustive(gen(rand))
        }

        "OneOf" {
            val gen = OneOf(IntegralType.interval(0,1), Just(2))
            val rand = Random()
            for(i in 0..4)
                IntegralType.exhaustive(gen(rand))
        }

        "ElementOf" {
            val gen = ElementOf<Int>(0, 1)
            val rand = Random()
            for(i in 0..2)
                IntegralType.exhaustive(gen(rand))
        }


    }
}
