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
import com.intellij.openapi.util.JDOMExternalizer
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import org.covscript.lang.*
import org.covscript.lang.module.COV_SDK_LIB_KEY
import org.covscript.lang.module.CovSdkType
import org.jdom.Element
import java.nio.file.Paths

class CovRunConfiguration(factory: CovRunConfigurationFactory, project: Project) :
		ModuleBasedConfiguration<RunConfigurationModule>(COV_NAME, RunConfigurationModule(project), factory) {
	private val covSdks = ProjectJdkTable.getInstance().getSdksOfType(CovSdkType.instance)
	private var sdkName = ""
	var sdkUsed = covSdks.firstOrNull { it.name == sdkName }
		set(value) {
			value?.let {
				sdkName = it.name
				field = it
			}
		}
	var workingDir = project.basePath.orEmpty()
	var targetFile = ""
	var covExecutive = sdkUsed?.run { Paths.get(homePath, "bin", "cs").toAbsolutePath().toString() }.orEmpty()
	var additionalParams = ""
	override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = CovRunConfigurationEditor(this)
	override fun getState(executor: Executor, environment: ExecutionEnvironment) = null
	override fun getValidModules() = allModules.filter { it.project.getUserData(COV_SDK_LIB_KEY) != null }
	override fun readExternal(element: Element) {
		super.readExternal(element)
		JDOMExternalizer.readString(element, "additionalParams")?.let { additionalParams = it }
		JDOMExternalizer.readString(element, "covExecutive")?.let { covExecutive = it }
		JDOMExternalizer.readString(element, "targetFile")?.let { targetFile = it }
		JDOMExternalizer.readString(element, "workingDir")?.let { workingDir = it }
		JDOMExternalizer.readString(element, "sdkName")?.let { sdkName = it }
		PathMacroManager.getInstance(project).collapsePathsRecursively(element)
	}

	override fun writeExternal(element: Element) {
		PathMacroManager.getInstance(project).expandPaths(element)
		super.writeExternal(element)
		JDOMExternalizer.write(element, "additionalParams", additionalParams)
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
	override fun getConfigurationTypeDescription() = COV_RUN_CONFIG_DESCRIPTION
	override fun getId() = COV_RUN_CONFIG_ID
	override fun getDisplayName() = COV_NAME
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
