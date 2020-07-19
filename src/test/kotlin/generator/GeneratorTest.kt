package generator

import io.kotest.core.spec.style.StringSpec
import org.kindone.proptest.Property
import org.kindone.proptest.Random
import org.kindone.proptest.generator.ArbitraryKotlinInt
import org.kindone.proptest.generator.ArbitraryKotlinString
import org.kindone.proptest.generator.IntegralType
import proptest.combinator.Construct
import proptest.generator.StringType

class GeneratorTest : StringSpec() {
    init {
//        "Map Property Test" {
//            val func = { a:Map<Int, Boolean> ->
//
//            }
//
//            val prop = Property(func)
//            prop.forAll()
//        }

        "String Property Test" {
            val func = { a:String ->
                println("a:" + a)
            }

            val prop = Property(func)
            prop.forAll()
        }

        "Construct1" {
            class SomeClass(val a:String) {

            }
            val stringGen = ArbitraryKotlinString()
            val someClassGen = Construct<SomeClass, String>(stringGen)
            val rand = Random()
            val shrinkable = someClassGen(rand)
            IntegralType.exhaustive(shrinkable)
        }

        "primitive" {
            val x = 5
            val y = 5.5
            println("x is int?: " + (x::class == Int::class))
            println("y is int?: " + (y::class == Int::class))
        }

        "Construct1.primitive" {
            class SomeClass(val i:Int) {
                override fun toString(): String {
                    return "SomeClass($i)"
                }
            }
            val intGen = IntegralType.fromTo(0,10)
            val someClassGen = Construct<SomeClass, Int>(intGen)
            val rand = Random()
            val shrinkable = someClassGen(rand)
            IntegralType.exhaustive(shrinkable)
        }

        "Construct2" {
            class SomeClass(val i:Int, val a:String) {
                override fun toString(): String {
                    return "SomeClass($i, \"$a\")"
                }
            }
            val intGen = IntegralType.fromTo(0,10)
            val stringGen = ArbitraryKotlinString()
            val someClassGen = Construct<SomeClass, Int, String>(intGen, stringGen)
            val rand = Random()
            val shrinkable = someClassGen(rand)
            IntegralType.exhaustive(shrinkable)
        }
    }
}
