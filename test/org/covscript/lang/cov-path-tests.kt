package org.covscript.lang

import java.nio.file.Paths

fun main(args: Array<String>) {
	println(Paths.get(".", "bin").toAbsolutePath())
	println(Paths.get("./", "bin").toAbsolutePath())
	println(Paths.get("../", "bin").toAbsolutePath())
	println(Paths.get("/home/", "bin").toAbsolutePath())
	println(Paths.get("/home", "bin").toAbsolutePath())
}