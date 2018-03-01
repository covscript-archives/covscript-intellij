package org.covscript.lang.execution

import com.intellij.execution.Executor
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.components.PathMacroManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMExternalizer
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import icons.CovIcons
import org.covscript.lang.*
import org.covscript.lang.module.covSettings
import org.jdom.Element

class CovRunConfiguration(
		project: Project, factory: CovRunConfigurationFactory) :
		LocatableConfigurationBase(project, factory, CovBundle.message("cov.name")) {
	var logPath = ""
	var importPaths = project.covSettings.settings.importPaths
	var logPathOption = false
	var importPathOption = false
	var compileOnlyOption = false
	var waitB4ExitOption = false
	var workingDir = ""
	var targetFile = ""
	var programArgs = ""
	var covExecutable = project.covSettings.settings.exePath
	override fun getConfigurationEditor() = CovRunConfigurationEditorImpl(this)
	override fun getState(executor: Executor, environment: ExecutionEnvironment) = CovCommandLineState(this, environment)
	override fun readExternal(element: Element) {
		super.readExternal(element)
		JDOMExternalizer.readString(element, "logPath")?.let { logPath = it }
		JDOMExternalizer.readString(element, "importPaths")?.let { importPaths = it }
		JDOMExternalizer.readBoolean(element, "logPathOption").let { logPathOption = it }
		JDOMExternalizer.readBoolean(element, "importPathOption").let { importPathOption = it }
		JDOMExternalizer.readBoolean(element, "compileOnlyOption").let { compileOnlyOption = it }
		JDOMExternalizer.readBoolean(element, "waitB4ExitOption").let { waitB4ExitOption = it }
		JDOMExternalizer.readString(element, "covExecutive")?.let { covExecutable = it }
		JDOMExternalizer.readString(element, "programArgs")?.let { programArgs = it }
		JDOMExternalizer.readString(element, "targetFile")?.let { targetFile = it }
		JDOMExternalizer.readString(element, "workingDir")?.let { workingDir = it }
		JDOMExternalizer.readString(element, "covExecutable")?.let { covExecutable = it }
		PathMacroManager.getInstance(project).collapsePathsRecursively(element)
	}

	override fun writeExternal(element: Element) {
		PathMacroManager.getInstance(project).expandPaths(element)
		super.writeExternal(element)
		JDOMExternalizer.write(element, "logPath", logPath)
		JDOMExternalizer.write(element, "importPaths", importPaths)
		JDOMExternalizer.write(element, "logPathOption", logPathOption)
		JDOMExternalizer.write(element, "importPathOption", importPathOption)
		JDOMExternalizer.write(element, "compileOnlyOption", compileOnlyOption)
		JDOMExternalizer.write(element, "waitB4ExitOption", waitB4ExitOption)
		JDOMExternalizer.write(element, "covExecutive", covExecutable)
		JDOMExternalizer.write(element, "programArgs", programArgs)
		JDOMExternalizer.write(element, "targetFile", targetFile)
		JDOMExternalizer.write(element, "workingDir", workingDir)
		JDOMExternalizer.write(element, "covExecutable", covExecutable)
	}
}

class CovRunConfigurationFactory(type: CovRunConfigurationType) : ConfigurationFactory(type) {
	override fun createTemplateConfiguration(project: Project) = CovRunConfiguration(project, this)
}

object CovRunConfigurationType : ConfigurationType {
	override fun getIcon() = CovIcons.COV_BIG_ICON
	override fun getConfigurationTypeDescription() = CovBundle.message("cov.run.config.description")
	override fun getId() = COV_RUN_CONFIG_ID
	override fun getDisplayName() = CovBundle.message("cov.name")
	override fun getConfigurationFactories() = arrayOf(CovRunConfigurationFactory(this))
}

class CovRunConfigurationProducer : RunConfigurationProducer<CovRunConfiguration>(CovRunConfigurationType) {
	override fun isConfigurationFromContext(
			configuration: CovRunConfiguration, context: ConfigurationContext) =
			configuration.targetFile == context
					.location
					?.virtualFile
					?.path

	override fun setupConfigurationFromContext(
			configuration: CovRunConfiguration, context: ConfigurationContext, ref: Ref<PsiElement>?): Boolean {
		val file = context.location?.virtualFile
		if (file?.fileType != CovFileType) return false
		configuration.name = file.nameWithoutExtension
		configuration.targetFile = file.path
		configuration.workingDir = context.project.basePath.orEmpty()
		return true
	}
}
