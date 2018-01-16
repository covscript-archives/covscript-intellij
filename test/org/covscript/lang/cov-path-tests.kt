package org.covscript.lang

import org.covscript.lang.module.versionOf
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
		versionOf(POSSIBLE_SDK_HOME_LINUX).let(::println)
	}
}
