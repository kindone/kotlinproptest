package proptest

import com.google.gson.*
import java.lang.reflect.Type

//
//class GsonInterfaceAdapter<T> : JsonSerializer<T>, JsonDeserializer<T> {
//    override fun serialize(
//        obj: T,
//        interfaceType: Type,
//        context: JsonSerializationContext
//    ): JsonElement {
//        val member = JsonObject()
//        member.addProperty("type", obj!!.javaClass.name)
//        member.add("data", context.serialize(obj))
//        return member
//    }
//
//    @Throws(JsonParseException::class)
//    override fun deserialize(
//        elem: JsonElement,
//        interfaceType: Type,
//        context: JsonDeserializationContext
//    ): T {
//        val member = elem as JsonObject
//        val typeString = get(member, "type")
//        val data = get(member, "data")
//        val actualType = typeForName(typeString)
//        return context.deserialize(data, actualType)
//    }
//
//    private fun typeForName(typeElem: JsonElement): Type {
//        return try {
//            Class.forName(typeElem.asString)
//        } catch (e: ClassNotFoundException) {
//            throw JsonParseException(e)
//        }
//    }
//
//    private operator fun get(wrapper: JsonObject, memberName: String): JsonElement {
//        return wrapper[memberName]
//            ?: throw JsonParseException(
//                "no '$memberName' member found in json file."
//            )
//    }
//}