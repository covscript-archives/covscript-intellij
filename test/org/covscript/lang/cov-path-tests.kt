package org.covscript.lang

import org.covscript.lang.module.*
import org.junit.Test
import java.nio.file.Paths

class PathTests {
	@Test
	fun sandbox() {
		println(Paths.get(".", "bin").toAbsolutePath())
		println(Paths.get("./", "bin").toAbsolutePath())
		println(Paths.get("../", "bin").toAbsolutePath())
		println(Paths.get("/home/", "bin").toAbsolutePath())
		println(Paths.get("/home", "bin").toAbsolutePath())
	}

	@Test
	fun version() {
		if (!System.getenv("CI").isNullOrBlank()) return
		versionOf(defaultCovExe).let(::println)
	}

	@Test
	fun codeExecutionTest() {
		if (!System.getenv("CI").isNullOrBlank()) return
		//language=CovScript
		executeInRepl(defaultCovExe, "system.out.println(2333)", 1000L).first.forEach(::println)
	}

	@Test
	fun codeExecutionTest2() {
		if (!System.getenv("CI").isNullOrBlank()) return
		//language=CovScript
		val (stdout, stderr) = executeInRepl(POSSIBLE_EXE_LINUX, """
system.out.println(2333)
system.out.println(1+1)
""".trimIndent(), 1000L)
		stdout.forEach(::println)
		stderr.forEach(::println)
	}
}
