package org.covscript.lang

import com.intellij.testFramework.ParsingTestCase
import org.junit.Test

class CovParserTest : ParsingTestCase("", COV_EXTENSION, CovParserDefinition()) {
	override fun getTestDataPath() = "testData"
	override fun skipSpaces() = true

	@Test
	fun testHello() {
		println(name)
		doTest(true)
	}

	@Test
	fun testNamespaces() {
		println(name)
		doTest(true)
	}
}
