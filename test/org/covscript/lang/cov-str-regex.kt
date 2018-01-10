package org.covscript.lang

import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern.compile

val pattern = compile(""""([^"\x00-\x1F\x7F\\]|\\[\\'"abfnrtv0])*"""")

fun taskStr1() {
	Runtime.getRuntime().exec(COV_VERSION_COMMAND)
			.also { it.waitFor(1000L, TimeUnit.MILLISECONDS) }
			.inputStream
			.let(::InputStreamReader)
			.let(::BufferedReader).readLine()
			.let(::println)
}

fun taskStr2() {
	(BigDecimal("233") + BigDecimal("233.23"))
			.let(BigDecimal::toPlainString)
			.let(::println)
}

fun main(args: Array<String>) {
	taskStr2()
}
