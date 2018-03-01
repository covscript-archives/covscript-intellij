package org.covscript.lang.module

import com.google.common.util.concurrent.SimpleTimeLimiter
import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.util.SystemInfo
import org.covscript.lang.*
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

const val GET_VERSION_TIME_PERIOD = 1500L
fun versionOf(exePath: String, timeLimit: Long = GET_VERSION_TIME_PERIOD): Pair<String, String> =
		executeInRepl(exePath,//language=CovScript
				"""
runtime.info()
runtime.get_import_path()""", timeLimit)
				.first
				.let {
					val version = it.firstOrNull { it.startsWith("version", true) }
							?.run { substringAfter(':').trim() }
							?: CovBundle.message("cov.modules.sdk.unknown-version")
					val import = it.lastOrNull()
							?.trim()
							?: Paths.get(exePath).parent.toString()
					version to import
				}

val defaultCovExe by lazy {
	PathEnvironmentVariableUtil.findInPath("cs_repl")?.run { path.removeSuffix("_repl") }
			?: PathEnvironmentVariableUtil.findInPath("cs_repl.exe")?.run { "${path.removeSuffix("_repl.exe")}.exe" }
			?: if (SystemInfo.isWindows) POSSIBLE_EXE_WINDOWS else POSSIBLE_EXE_LINUX
}

/**
 * @param exePath the home path of the CovScript SDK currently used
 * @param code doesn't need to `system.exit(0)`, because this function will automatically add one
 * @param timeLimit the time limit. Will wait for this long and kill process after 100 ms
 * @return (stdout, stderr)
 */
fun executeInRepl(
		exePath: String,
		code: String,
		timeLimit: Long = GET_VERSION_TIME_PERIOD): Pair<List<String>, List<String>> {
	var processRef: Process? = null
	var output: List<String> = emptyList()
	var outputErr: List<String> = emptyList()
	try {
		val path = replPathByExe(exePath)
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

private fun replPathByExe(exePath: String) = exePath.removeSuffix(".exe").let {
	if (SystemInfo.isWindows) "${it}_repl.exe" else "${it}_repl"
}

private fun collectLines(it: InputStream) = it.bufferedReader().useLines(Sequence<String>::toList)
fun validateCovExe(settings: CovSettings) = validateCovExe(settings.exePath)
fun validateCovExe(pathString: String) = Files.isExecutable(Paths.get(pathString))