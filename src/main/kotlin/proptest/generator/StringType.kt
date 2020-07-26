package proptest.generator

import org.kindone.proptest.Generator
import org.kindone.proptest.generator.IntegralType

object StringType {
    fun genASCII():Generator<Char> {
        return IntegralType.interval(0x1, 0x7F).map { it.toChar() }
    }

    fun genUnicode():Generator<Char> {
        return IntegralType.interval(0x1, 0x10FFFF).map { it.toChar() }
    }
}