package generator

import io.kotest.core.spec.style.StringSpec
import org.kindone.proptest.Property

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
    }
}