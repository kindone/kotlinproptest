import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.kindone.proptest.generator.ArbitraryKotlinCollectionsList
import org.kindone.proptest.generator.ArbitraryKotlinInt
import org.kindone.proptest.Property
import org.kindone.proptest.Random
import org.kindone.proptest.generator.Util
import kotlin.reflect.KClass
import kotlin.reflect.jvm.reflect

class BasicTest : StringSpec() {
    init {
        "strings.length should return size of string" {
            "hello".length shouldBe   5
        }

        "cloning random" {
            val mrand1 = org.kindone.proptest.Random(5)
            mrand1.nextLong()
            mrand1.nextLong()
            val mrand2:org.kindone.proptest.Random = mrand1.clone()
            mrand2.nextLong() shouldBe mrand1.nextLong()
        }

        "property test" {
            val f:(Int,Int) -> Unit = { a:Int, b:Int ->
                (a + b) shouldBe (b + a)
            }
            val prop = Property(f)
            prop.forAll()
        }

        "generic reflection" {
            val kc:KClass<*> = Int::class
            println(kc.qualifiedName)
            val kcg = List::class
            println(kcg.qualifiedName)

//            val kcgt = List<Int>::class.createType()
            extract<List<Int>>()
        }

        "lambda test" {
            val func1 = { a:Int, b:Int -> Unit
                (a + b) shouldBe (b + a)
            }
            val kf = func1.reflect()!!
            for(param in kf.parameters) {
                println(param.type.classifier)
                for(typeargs in param.type.arguments) {
                    println(typeargs.type)
                }
            }
            println(kf.returnType)

            val func2 = { a:List<List<Int>> -> Unit
            }

            val kf2 = func2.reflect()!!
            for(param in kf2.parameters) {
                println("classifier:" + param.type.classifier)
                for(typeargs in param.type.arguments) {
                    println("argument: " + typeargs.type)
                }
            }

            val prop = Property(func2)
            prop.forAll()

            val a:List<Int> = emptyList()
        }

        "binarySearchShrinkable1" {
            val shrinkable = Util.binarySearchShrinkable(8)
            Util.exhaustive(shrinkable)
        }

        "binarySearchShrinkable2" {
            val shrinkable = Util.binarySearchShrinkable(-8)
            Util.exhaustive(shrinkable)
        }

        "shrink list" {
            val listGen = ArbitraryKotlinCollectionsList<Int>(ArbitraryKotlinInt())
            val rand = Random()
            val shrinkable = listGen(rand)
            Util.exhaustive(shrinkable)
        }
    }

    inline fun <reified T:Any> extract() {
        println(T::class.qualifiedName)
    }

}
