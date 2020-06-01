import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import org.kindone.proptest.Generator
import org.kindone.proptest.KRandom
import org.kindone.proptest.Property
import org.kindone.proptest.Shrinkable
//import proptest.GsonInterfaceAdapter
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.reflect

val primitive1 = { a:Int ->
    println(a+5)
    Unit
}

val primitive = { a:Int, b:Int ->
    println(a+b+5)
    Unit
}

val complex = { a:String ->
    Unit
}

fun toCamelcase(fqn:String):String {
    val camelcase = fqn.replace("""^[a-z]|\b""".toRegex()) {
        it.value.toUpperCase()
    }
    val dotRemoved = camelcase.replace(".", "")
    return dotRemoved
}


val fprimitive:KFunction<Unit> = primitive.reflect()!!
val fcomplex:KFunction<Unit> = complex.reflect()!!

class NoUsableConstructor: Error()

fun makeRandomInstance(clazz: KClass<*>): Any? {
    val constructor = clazz.constructors
        .minBy { it.parameters.size } ?: throw NoUsableConstructor()

    val arguments = constructor.parameters
        .map { it.type.classifier as KClass<*> }
        .map { makeRandomInstance(it) }
        .toTypedArray()

    return constructor.call(*arguments)
}

fun <Arbitrary:Any> makeArbitrary(arbcls: KClass<Arbitrary>): Arbitrary {
    val arbitrary = arbcls.primaryConstructor!!.call()
    return arbitrary
}

val intGen = object: Generator<Int> {
    override operator fun invoke(random:org.kindone.proptest.Random) : Shrinkable<Int> {
        return Shrinkable<Int>(5)
    }
}

println(Property.toCamelcase("asdf.cddf"))
println(Property.invoke(primitive).forAll())
println(Property.invoke(primitive1, intGen).forAll())

fun <T: Any> cast(any: Any, clazz: KClass<out T>): T = clazz.javaObjectType.cast(any)
//
//
//val r = Random(42)
//val r2:java.util.Random = r.asJavaRandom()
//val r3 = java.util.Random()
//

val krand1 = KRandom(6)
println(krand1.toString())
println(krand1.nextLong())
println(krand1.toString())
println(krand1.nextLong())
val krand2 = krand1.clone()
val x = krand1.x
println(krand1.x)
println(x)
val krand3 = KRandom(krand1.x, krand1.y, krand1.z, krand1.w, krand1.v, krand1.addend)
println(krand1.equals(krand2))
println(krand1.equals(krand3))
println(krand1.toString())
println(krand2.toString())
println(krand3.toString())

println(krand2.nextLong())
println(krand1.nextLong())
println(krand2.nextLong())
println(krand2.nextLong())


val gsonBuilder  = GsonBuilder()
class KRandomCreator : InstanceCreator<KRandom> {
    override fun createInstance(type:Type):KRandom {
        return KRandom(0)
    }
}
//
////gsonBuilder.registerTypeAdapter(kotlin.random.Random::class.java, KRandomCreator())
//gsonBuilder.registerTypeAdapter(KRandom::class.java, GsonInterfaceAdapter<KRandom>())
//val gson = gsonBuilder.create()
//
//fun cloneKRandom(rand:kotlin.random.Random): kotlin.random.Random {
//    return gson.fromJson(gson.toJson(rand), kotlin.random.Random::class.java)
//}
//
//fun cloneRandom(rand:java.util.Random): java.util.Random {
//    return gson.fromJson(gson.toJson(rand), java.util.Random::class.java)
////    val bo = ByteArrayOutputStream()
////    val oos = ObjectOutputStream(bo)
////    oos.writeObject(rand)
////    oos.close()
////    println(bo.size())
////    val ois = ObjectInputStream(
////        ByteArrayInputStream(bo.toByteArray())
////    )
////    return ois.readObject() as java.util.Random
//}
//val jrand1 = java.util.Random(5)
//println(jrand1.nextLong())
//println(jrand1.nextLong())
//val jrand2 = cloneRandom(jrand1)
//println(jrand1.equals(jrand2))
//println(jrand1.nextLong())
//println(jrand2.nextLong())
//
//
//val random = Random(5)
//println(random.nextLong())
//println(random.nextLong())
//val random2 = gson.fromJson(gson.toJson(random), kotlin.random.Random::class.java)
//println(random.nextLong())
//println(random2.nextLong())
//println(random2.nextLong())

for(p in fcomplex.parameters) {
    val kc = p.type.classifier as KClass<*>
//    println(String::class.companionObject!!.memberExtensionFunctions)
    println(kc.companionObject)
}
