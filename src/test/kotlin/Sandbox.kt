import com.google.gson.GsonBuilder
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.kindone.proptest.Property

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
            mrand1.getNext()
            mrand1.getNext()
            val mrand2:org.kindone.proptest.Random = mrand1.clone()
            mrand2.getNext() shouldBe mrand1.getNext()
        }

        "property test" {
            val prop = Property({ a:Int, b:Int ->
                (a + b) shouldBe (b + a)
            })
            prop.forAll()
        }
    }
}