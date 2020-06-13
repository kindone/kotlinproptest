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
    fun nextBoolean():Boolean = rand.nextBoolean()
    fun nextFloat():Float = rand.nextFloat()
    fun nextDouble():Double = rand.nextDouble()

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