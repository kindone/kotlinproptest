package proptest.combinator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import java.lang.RuntimeException

class ElementOf {
    data class Weighted<T>(val obj:T, val weight:Double)

    companion object {
        inline operator fun<T> invoke(vararg values: Any):Generator<T> {
            var sum  = 0.0
            var numUnassigned = 0
            var weightedGenerators = values.map { value ->
                if(value is Weighted<*>) {
                    sum += value.weight
                    value
                }
                else {
                    numUnassigned += 1
                    Weighted(value, 0.0)
                }
            }

            if(sum < 0.0 || sum  >= 1.0)
                throw RuntimeException("invalid weights")

            if(numUnassigned > 0) {
                val rest = 1.0 - sum
                val perUnussigned = rest  / numUnassigned

                weightedGenerators = weightedGenerators.map { weightedGenerator ->
                    if(weightedGenerator.weight == 0.0) {
                        Weighted(weightedGenerator.obj, perUnussigned)
                    }
                    else
                        weightedGenerator
                }
            }
            return object : Generator<T>() {
                override fun invoke(random: Random): Shrinkable<T> {
                    while(true) {
                        val dice = random.inRange(0, weightedGenerators.size)
                        if(random.nextBoolean(weightedGenerators[dice].weight)) {
                            return Shrinkable(weightedGenerators[dice].obj as T)
                        }
                    }
                    weightedGenerators
                }
            }

        }
    }
}