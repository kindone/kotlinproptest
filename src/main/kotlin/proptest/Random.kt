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
    // TODO
    fun nextBoolean(trueProb:Double = 0.5):Boolean = rand.nextBoolean()
    fun nextFloat():Float = rand.nextFloat()
    fun nextDouble():Double = rand.nextDouble()

    fun fromTo(min:Int, max:Int):Int = min + (Math.abs(nextLong().toInt()) % (max+1-min))
    fun fromTo(min:Long, max:Long):Long = min + (Math.abs(nextLong()) % (max+1-min))
    fun inRange(from:Int, to:Int):Int = from + (Math.abs(nextLong().toInt()) % (to-from))
    fun inRange(from:Long, to:Long):Long = from + (Math.abs(nextLong()) % (to-from))

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