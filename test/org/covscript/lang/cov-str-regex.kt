package org.covscript.lang

import java.util.regex.Pattern

fun main(args: Array<String>) {
	val pattern = Pattern.compile(""""([^"\x00-\x1F\x7F\\]|\\[\\'"abfnrtv0])*"""")
}
