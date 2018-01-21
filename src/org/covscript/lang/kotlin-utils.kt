package org.covscript.lang

internal inline fun forceRun(lambda: () -> Any) {
	try {
		lambda()
	} catch (e: Throwable) {
	}
}
