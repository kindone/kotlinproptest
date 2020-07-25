package proptest.combinator

import org.kindone.proptest.Generator
import org.kindone.proptest.Random
import org.kindone.proptest.Shrinkable
import java.lang.RuntimeException

class OneOf {
    class Weighted<T>(val gen:Generator<T>, val weight:Double) :  Generator<T>() {
        override fun invoke(random: Random): Shrinkable<T> {
            return gen(random)
        }

    }

    companion object {
        inline operator fun<T> invoke(generators: List<Generator<T>>):Generator<T> {
            var sum  = 0.0
            var numUnassigned = 0
            var weightedGenerators = generators.map { generator ->
                if(generator is Weighted<T>) {
                    sum += generator.weight
                    generator
                }
                else {
                    numUnassigned += 1
                    Weighted(generator, 0.0)
                }
            }

            if(sum < 0.0 || sum  >= 1.0)
                throw RuntimeException("invalid weights")

            if(numUnassigned > 0) {
                val rest = 1.0 - sum
                val perUnussigned = rest  / numUnassigned

                weightedGenerators = weightedGenerators.map { weightedGenerator ->
                    if(weightedGenerator.weight == 0.0) {
                        Weighted(weightedGenerator.gen, sum)
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
                            return weightedGenerators[dice].gen(random)
                        }
                    }
                    weightedGenerators
                }
            }

        }
    }
}