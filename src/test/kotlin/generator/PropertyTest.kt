package generator

import io.kotest.core.spec.style.StringSpec
import org.kindone.proptest.Property
import proptest.AssertFailed

class PropertyTest : StringSpec() {
    init {
        "Map Property Test" {
            val func = { a:Map<Int, Boolean> ->
                if(a.size > 2)
                    throw AssertFailed("size too big")
            }

            val prop = Property(func)
            prop.forAll()
        }

        "String Property Test" {
            val func = { a:String ->
                println("a:" + a)
            }

            val prop = Property(func)
            prop.forAll()
        }
    }
}