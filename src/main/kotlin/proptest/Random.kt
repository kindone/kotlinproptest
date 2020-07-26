package org.kindone.proptest

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class Random(val rand:java.util.Random) {

    constructor() : this(java.util.Random(0)) {
    }

    constructor(seed:Long) : this(java.util.Random(seed)) {
    }

    fun nextLong():Long = rand.nextLong()
    fun nextInt():Int = rand.nextInt()
    fun nextBoolean(trueProb:Double = 0.5):Boolean = inRange(0L, 10000000000L) < (10000000000L * trueProb)
    fun nextFloat():Float = rand.nextFloat()
    fun nextDouble():Double = rand.nextDouble()

    fun interval(min:Int, max:Int):Int = min + (Math.abs(nextLong().toInt()) % (max+1-min))
    fun interval(min:Long, max:Long):Long = min + (Math.abs(nextLong()) % (max+1-min))
    fun inRange(fromInclusive:Int, toExclusive:Int):Int = fromInclusive + (Math.abs(nextLong().toInt()) % (toExclusive-fromInclusive))
    fun inRange(fromInclusive:Long, toExclusive:Long):Long = fromInclusive + (Math.abs(nextLong()) % (toExclusive-fromInclusive))

    fun clone():Random {
        val bo = ByteArrayOutputStream()
        val oos = ObjectOutputStream(bo)
        oos.writeObject(rand)
        oos.flush()
        oos.close()
        val ois = ObjectInputStream(
            ByteArrayInputStream(bo.toByteArray())
        )
       return Random(ois.readObject() as java.util.Random)
    }
}