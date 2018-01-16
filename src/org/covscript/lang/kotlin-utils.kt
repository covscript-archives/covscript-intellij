package org.covscript.lang


inline fun forceRun(lambda: () -> Unit) {
	try {
		lambda()
	} catch (e: Throwable) {
	}
}
