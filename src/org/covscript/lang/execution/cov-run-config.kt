package org.covscript.lang.execution

import com.intellij.execution.Executor
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.components.PathMacroManager
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.JDOMExternalizer
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import org.covscript.lang.*
import org.covscript.lang.module.CovSdkType
import org.jdom.Element
import java.nio.file.Paths

class CovRunConfiguration(factory: CovRunConfigurationFactory, project: Project) :
		ModuleBasedConfiguration<RunConfigurationModule>(CovBundle.message("cov.name"), RunConfigurationModule(project), factory) {
	private val covSdks get() = ProjectJdkTable.getInstance().getSdksOfType(CovSdkType.instance)
	private var sdkName = ""
	var sdkUsed = ProjectRootManager.getInstance(project).projectSdk
		set(value) {
			value?.let {
				sdkName = it.name
				field = it
				covExecutive = Paths.get(it.homePath, "bin", "cs").toAbsolutePath().toString()
			}
		}
	var logPath = ""
	var importPath = ""
	var logPathOption = false
	var importPathOption = false
	var compileOnlyOption = false
	var waitB4ExitOption = false
	var workingDir = ""
	var targetFile = ""
	var covExecutive = sdkUsed?.run { Paths.get(homePath, "bin", "cs").toAbsolutePath().toString() }.orEmpty()
	override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = CovRunConfigurationEditor(this)
	override fun getState(executor: Executor, environment: ExecutionEnvironment) = CovCommandLineState(this, environment)
	override fun getValidModules() = allModules.filter { ProjectRootManager.getInstance(it.project).projectSdk?.sdkType is CovSdkType }
	override fun readExternal(element: Element) {
		super.readExternal(element)
		JDOMExternalizer.readString(element, "logPath")?.let { logPath = it }
		JDOMExternalizer.readString(element, "importPath")?.let { importPath = it }
		JDOMExternalizer.readBoolean(element, "logPathOption").let { logPathOption = it }
		JDOMExternalizer.readBoolean(element, "importPathOption").let { importPathOption = it }
		JDOMExternalizer.readBoolean(element, "compileOnlyOption").let { compileOnlyOption = it }
		JDOMExternalizer.readBoolean(element, "waitB4ExitOption").let { waitB4ExitOption = it }
		JDOMExternalizer.readString(element, "covExecutive")?.let { covExecutive = it }
		JDOMExternalizer.readString(element, "targetFile")?.let { targetFile = it }
		JDOMExternalizer.readString(element, "workingDir")?.let { workingDir = it }
		JDOMExternalizer.readString(element, "sdkName")?.let { name ->
			sdkUsed = covSdks.firstOrNull { it.name == name } ?: return@let
		}
		PathMacroManager.getInstance(project).collapsePathsRecursively(element)
	}

	override fun writeExternal(element: Element) {
		PathMacroManager.getInstance(project).expandPaths(element)
		super.writeExternal(element)
		JDOMExternalizer.write(element, "logPath", logPath)
		JDOMExternalizer.write(element, "importPath", importPath)
		JDOMExternalizer.write(element, "logPathOption", logPathOption)
		JDOMExternalizer.write(element, "importPathOption", importPathOption)
		JDOMExternalizer.write(element, "compileOnlyOption", compileOnlyOption)
		JDOMExternalizer.write(element, "waitB4ExitOption", waitB4ExitOption)
		JDOMExternalizer.write(element, "covExecutive", covExecutive)
		JDOMExternalizer.write(element, "targetFile", targetFile)
		JDOMExternalizer.write(element, "workingDir", workingDir)
		JDOMExternalizer.write(element, "sdkName", sdkName)
	}
}

class CovRunConfigurationFactory(type: CovRunConfigurationType) : ConfigurationFactory(type) {
	override fun createTemplateConfiguration(project: Project) = CovRunConfiguration(this, project)
}

object CovRunConfigurationType : ConfigurationType {
	override fun getIcon() = COV_BIG_ICON
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
		if (context.psiLocation?.containingFile !is CovFile) return false
		configuration.targetFile = context.location?.virtualFile?.path.orEmpty()
		configuration.workingDir = context.project.basePath.orEmpty()
		return true
	}
}
