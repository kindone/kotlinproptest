package proptest.type

class NotNull<T:Any>(val obj:T) : Nullable<T>(false) {
}