package org.kindone.proptest


class KRandom(
    var x: Int,
    var y: Int,
    var z: Int,
    var w: Int,
    var v: Int,
    var addend: Int
) : kotlin.random.Random() {

    constructor(seed: Long) : this(seed.toInt(), seed.shr(32).toInt())
    constructor(seed1: Int, seed2: Int) :
            this(seed1, seed2, 0, 0, seed1.inv(), (seed1 shl 10) xor (seed2 ushr 4)) {

        // some trivial seeds can produce several values with zeroes in upper bits, so we discard first 64
        repeat(64) { nextInt() }
    }

    constructor() : this(0)

    init {
        require((x or y or z or w or v) != 0) { "Initial state must have at least one non-zero element." }
    }

    override fun nextInt(): Int {
        // Equivalent to the xorxow algorithm
        // From Marsaglia, G. 2003. Xorshift RNGs. J. Statis. Soft. 8, 14, p. 5
        var t = x
        t = t xor (t ushr 2)
        x = y
        y = z
        z = w
        val v0 = v
        w = v0
        t = (t xor (t shl 1)) xor v0 xor (v0 shl 4)
        v = t
        addend += 362437
        return t + addend
    }

    override fun nextBits(bitCount: Int): Int {
        val intval = nextInt()
        return intval.ushr(32 - bitCount) and (-bitCount).shr(31)
    }

    override fun equals(other: Any?): Boolean {
        if(other !is KRandom)
            return false
        val k = other as KRandom
        return x == k.x && k.y == y && k.z == z && k.w == w && k.v == v && k.addend == addend
    }

    override fun toString(): String {
        return "KRandom(x: $x, y:$y, z: $z, w: $w, v: $v, addend:$addend)"
    }

    fun clone():KRandom {
        return KRandom(x, y, z, w, v, addend)
    }
}