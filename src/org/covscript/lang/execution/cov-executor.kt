package org.covscript.lang.execution

import com.intellij.execution.*
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleViewContentType
import java.nio.charset.Charset

class CovCommandLineState(
		private val configuration: CovRunConfiguration,
		env: ExecutionEnvironment) : CommandLineState(env) {
	override fun startProcess() = OSProcessHandler(GeneralCommandLine(listOf(
			listOf(configuration.covExecutive),
			configuration.additionalParams.split(' ').filter(String::isNotBlank),
			listOf(configuration.targetFile)
	).flatMap { it }).also {
		it.withCharset(Charset.forName("UTF-8"))
		it.withWorkDirectory(configuration.workingDir)
	}).also {
		ProcessTerminatedListener.attach(it)
		it.startNotify()
	}

	override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
		val handler = startProcess()
		val console = createConsole(executor)
		console?.run {
			print("${handler.commandLine}\n", ConsoleViewContentType.NORMAL_OUTPUT)
			attachToProcess(handler)
		}
		return DefaultExecutionResult(console, handler, *createActions(console, handler, executor))
	}
}
