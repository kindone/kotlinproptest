package generator

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldHave
import io.kotest.matchers.shouldNotBe
import org.junit.Assert
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

        "kotlin assert Failure" {
            val prop = Property({ a:String ->
                assert(a != "")
            })
            prop.forAll()
        }

        "StringSpec Assertion failure" {
            val func = { a:String ->
                a.shouldNotBe("")
            }

            val prop = Property(func)
            prop.forAll()
        }

        "JUnit Assertion failure" {
            val func = { a:String ->
                Assert.assertNotEquals(a, "")
            }

            val prop = Property(func)
            prop.forAll()
        }
    }
}