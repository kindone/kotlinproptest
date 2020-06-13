import com.google.gson.GsonBuilder
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.kindone.proptest.Property
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

class BasicTest : StringSpec() {
    init {
        "strings.length should return size of string" {
            "hello".length shouldBe   5
        }

        "cloning random" {
            val gsonBuilder  = GsonBuilder()
            /*
            class KRandomCreator : InstanceCreator<Random> {
                override fun createInstance(type: Type): Random {
                    return Random(0)
                }
            }

            gsonBuilder.registerTypeAdapter(kotlin.random.Random::class.java, KRandomCreator())
            val rand1 = Random(6)
            println(rand1.nextInt())
            println(rand1.nextInt())
            val rand2:Random = gson.fromJson(gson.toJson(rand1), kotlin.random.Random::class.java)
            println(rand2.nextInt())
            println(rand1.nextInt())
            */

            val gson = gsonBuilder.create()


            val jrand1 = java.util.Random(5)
            jrand1.nextInt()
            jrand1.nextInt()
            val jrand2:java.util.Random = gson.fromJson(gson.toJson(jrand1), java.util.Random::class.java)
            jrand2.nextInt() shouldBe jrand1.nextInt()

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

            val kf = f::invoke
            println(kf.typeParameters)
            println(kf.parameters)
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
    }

    inline fun <reified T:Any> extract() {
        println(T::class.qualifiedName)
    }
}