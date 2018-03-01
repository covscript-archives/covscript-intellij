package org.covscript.lang.execution

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.ConfigurationException
import org.covscript.lang.CovBundle
import org.covscript.lang.CovFileType
import org.jetbrains.annotations.Contract
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.JPanel

class CovRunConfigurationEditorImpl(configuration: CovRunConfiguration) :
		CovRunConfigurationEditor() {

	init {
		logPath.addChangeListener { logPathField.isEnabled = logPath.isSelected }
		importPath.addChangeListener { importPathField.isEnabled = importPath.isSelected }
		covExecutiveField.addBrowseFolderListener(
				CovBundle.message("cov.messages.run.select-interpreter"),
				CovBundle.message("cov.messages.run.select-interpreter.description"),
				null,
				FileChooserDescriptorFactory.createSingleFileDescriptor())
		workingDirField.addBrowseFolderListener(
				CovBundle.message("cov.messages.run.select-working-dir"),
				CovBundle.message("cov.messages.run.select-working-dir.description"), null,
				FileChooserDescriptorFactory.createSingleFolderDescriptor())
		targetFileField.addBrowseFolderListener(
				CovBundle.message("cov.messages.run.select-cov-file"),
				CovBundle.message("cov.messages.run.select-cov-file.description"), null,
				FileChooserDescriptorFactory.createSingleFileDescriptor(CovFileType))
		resetEditorFrom(configuration)
	}

	override fun resetEditorFrom(configuration: CovRunConfiguration) {
		logPath.isSelected = configuration.logPathOption
		importPath.isSelected = configuration.importPathOption
		compileOnly.isSelected = configuration.compileOnlyOption
		waitBeforeExit.isSelected = configuration.waitB4ExitOption
		logPathField.text = configuration.logPath
		logPathField.isEnabled = logPath.isSelected
		importPathField.text = configuration.importPaths
		importPathField.isEnabled = importPath.isSelected
		covExecutiveField.text = configuration.covExecutable
		targetFileField.text = configuration.targetFile
		workingDirField.text = configuration.workingDir
		progArgsField.text = configuration.programArgs
	}

	@Throws(ConfigurationException::class)
	override fun applyEditorTo(configuration: CovRunConfiguration) {
		val logPathOp = logPath.isSelected
		configuration.logPathOption = logPathOp
		val importPathOp = importPath.isSelected
		configuration.importPathOption = importPathOp
		configuration.compileOnlyOption = compileOnly.isSelected
		configuration.waitB4ExitOption = waitBeforeExit.isSelected
		if (importPathOp) configuration.importPaths = importPathField.text.let {
			it.takeIf { Files.isDirectory(Paths.get(it)) } ?: reportInvalidPath(it)
		}
		if (logPathOp) configuration.logPath = logPathField.text.let {
			it.takeIf { Files.isDirectory(Paths.get(it).parent) } ?: reportInvalidPath(it)
		}
		configuration.covExecutable = covExecutiveField.text.let {
			it.takeIf { Files.isExecutable(Paths.get(it)) } ?: reportInvalidPath(it)
		}
		configuration.targetFile = targetFileField.text.let {
			it.takeIf { Files.isReadable(Paths.get(it)) } ?: reportInvalidPath(it)
		}
		configuration.workingDir = workingDirField.text.let {
			it.takeIf { Files.isDirectory(Paths.get(it)) } ?: reportInvalidPath(it)
		}
		configuration.programArgs = progArgsField.text
	}

	@Contract("_ -> fail")
	@Throws(ConfigurationException::class)
	private fun reportInvalidPath(path: String): Nothing {
		throw ConfigurationException(CovBundle.message("cov.run.config.invalid-path", path))
	}

	override fun createEditor(): JPanel {
		return mainPanel
	}
}

