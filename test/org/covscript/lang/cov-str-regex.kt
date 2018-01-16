package org.covscript.lang

import java.math.BigDecimal
import java.util.regex.Pattern.compile

val pattern = compile(""""([^"\x00-\x1F\x7F\\]|\\[\\'"abfnrtv0])*"""")

fun taskStr2() {
	(BigDecimal("233") + BigDecimal("233.23"))
			.let(BigDecimal::toPlainString)
			.let(::println)
}

fun main(args: Array<String>) {
	taskStr2()
}
