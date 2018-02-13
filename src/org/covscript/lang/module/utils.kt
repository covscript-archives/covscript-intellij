package org.covscript.lang.module

import com.google.common.util.concurrent.SimpleTimeLimiter
import org.covscript.lang.CovBundle
import org.covscript.lang.forceRun
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

const val GET_VERSION_TIME_PERIOD = 500L
//language=CovScript
fun versionOf(homePath: String, timeLimit: Long = GET_VERSION_TIME_PERIOD) =
		executeInRepl(homePath, "runtime.info()", timeLimit)
				.first
				.firstOrNull { it.startsWith("version", true) }
				?.run { substringAfter(':').trim() }
				?: CovBundle.message("cov.modules.sdk.unknown-version")

/**
 * @param homePath the home path of the CovScript SDK currently used
 * @param code doesn't need to `system.exit(0)`, because this function will automatically add one
 * @param timeLimit the time limit. Will wait for this long and kill process after 100 ms
 * @return (stdout, stderr)
 */
fun executeInRepl(homePath: String, code: String, timeLimit: Long): Pair<List<String>, List<String>> {
	var processRef: Process? = null
	var output: List<String> = emptyList()
	var outputErr: List<String> = emptyList()
	try {
		val path = Paths.get(homePath, "bin", "cs_repl").toAbsolutePath().toString()
		SimpleTimeLimiter().callWithTimeout({
			val process: Process = Runtime.getRuntime().exec("$path --silent")
			processRef = process
			process.outputStream.use {
				it.write("$code\nsystem.exit(0)".toByteArray())
				it.flush()
			}
			process.waitFor(timeLimit, TimeUnit.MILLISECONDS)
			output = process.inputStream.use(::collectLines)
			outputErr = process.errorStream.use(::collectLines)
			forceRun(process::destroy)
		}, timeLimit + 100, TimeUnit.MILLISECONDS, true)
	} catch (e: Throwable) {
		processRef?.destroy()
	}
	return output to outputErr
}

private fun collectLines(it: InputStream): List<String> {
	val reader = it.bufferedReader()
	val ret = reader.lines().collect(Collectors.toList())
	forceRun(reader::close)
	return ret
}

fun validateCovHome(settings: CovSettings) = settings.version != CovBundle.message("cov.modules.sdk.unknown-version")
fun validateCovHome(pathString: String) = (Files.isExecutable(Paths.get(pathString, "bin", "cs")) or
		Files.isExecutable(Paths.get(pathString, "bin", "cs.exe"))) and
		(Files.isExecutable(Paths.get(pathString, "bin", "cs_repl")) or
				Files.isExecutable(Paths.get(pathString, "bin", "cs_repl.exe")))
