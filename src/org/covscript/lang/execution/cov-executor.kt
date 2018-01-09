package org.covscript.lang.execution

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment

class CovCommandLineState(
		private val configuration: CovRunConfiguration,
		env: ExecutionEnvironment) : CommandLineState(env) {
	override fun startProcess() = OSProcessHandler(GeneralCommandLine(listOf(
			listOf(configuration.covExecutive),
			configuration.additionalParams.split(' ').filter(String::isNotBlank),
			listOf(configuration.targetFile)
	).flatMap { it }).apply {
		setWorkDirectory(configuration.workingDir)
	}).also {
		ProcessTerminatedListener.attach(it)
		it.startNotify()
	}
}
