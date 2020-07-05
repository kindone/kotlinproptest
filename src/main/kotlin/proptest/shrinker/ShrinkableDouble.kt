package proptest.shrinker

import org.kindone.proptest.Shrinkable
import org.kindone.proptest.generator.IntegralType

fun ShrinkableDouble(value:Double):Shrinkable<Double> {
    return Shrinkable(value).with { ShrinkableSequenceOfDouble(value) }
}

fun ShrinkableSequenceOfDouble(value:Double):Sequence<Shrinkable<Double>> {
    if(value == 0.0) {
        return emptySequence<Shrinkable<Double>>()
    }
    else if(value == Double.NaN) {
        return listOf(Shrinkable(0.0)).asSequence()
    }
    else {
        var fraction = 0.0
        var exponent = 0
        if(value == Double.POSITIVE_INFINITY) {
            val max = Double.MAX_VALUE
            exponent = Math.getExponent(max)
            fraction = getMantissa(max)
        }
        else if(value == Double.NEGATIVE_INFINITY) {
            val min = Double.MIN_VALUE
            exponent = Math.getExponent(min)
            fraction = getMantissa(min)
        }
        else {
            exponent = Math.getExponent(value)
            fraction = getMantissa(value)
        }

        val expShrinkable = IntegralType.binarySearchShrinkable(exponent)
        var doubleShrinkable = expShrinkable.transform { value ->
            fraction * Math.pow(2.0, value.toDouble())
        }
        // prepend 0.0
        doubleShrinkable = doubleShrinkable.with {
            listOf(Shrinkable(0.0)).asSequence().plus(doubleShrinkable)
        }

        doubleShrinkable = doubleShrinkable.andThen { shr ->
            val value = shr.value
            val exponent = Math.getExponent(value)
            if(value == 0.0)
                emptySequence()
            else if(value > 0)
                listOf(Shrinkable(0.5 * Math.pow(2.0, exponent.toDouble()))).asSequence()
            else
                listOf(Shrinkable(-0.5 * Math.pow(2.0, exponent.toDouble()))).asSequence()
        }

        doubleShrinkable = doubleShrinkable.andThen { shr ->
            val value = shr.value
            val intValue = value.toInt()
            if(intValue != 0 && Math.abs(intValue) < Math.abs(value))
                listOf(Shrinkable(intValue.toDouble())).asSequence()
            else
                emptySequence()
        }
        return doubleShrinkable.shrinks()
    }
}

fun getMantissa(x: Double): Double {
    val exponent = Math.getExponent(x)
    return x / Math.pow(2.0, exponent.toDouble())
}