package generator

import io.kotest.core.spec.style.StringSpec
import org.kindone.proptest.Property
import proptest.combinator.Construct

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

        "Construct" {
            class SomeClass(val a:Int) {

            }
            Construct<SomeClass>()
            Construct<SomeClass, Int>(10)
        }
    }
}
