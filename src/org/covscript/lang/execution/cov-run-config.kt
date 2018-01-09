package org.covscript.lang.execution

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import org.covscript.lang.*

class CovRunConfiguration(factory: CovRunConfigurationFactory, project: Project) :
		ModuleBasedConfiguration<RunConfigurationModule>(
				COV_NAME,
				RunConfigurationModule(project),
				factory) {
	override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = CovRunConfigurationEditor()
	override fun getState(executor: Executor, environment: ExecutionEnvironment) = null
	override fun getValidModules() = allModules.filter { it.project.getUserData(COV_SDK_LIB_KEY) != null }
}

class CovRunConfigurationFactory : ConfigurationFactory(CovRunConfigurationType) {
	override fun createTemplateConfiguration(project: Project) = CovRunConfiguration(this, project)
}

object CovRunConfigurationType : ConfigurationType {
	override fun getIcon() = COV_BIG_ICON
	override fun getConfigurationTypeDescription() = COV_RUN_CONFIG_DESCRIPTION
	override fun getId() = COV_RUN_CONFIG_ID
	override fun getDisplayName() = COV_NAME
	override fun getConfigurationFactories() = arrayOf(CovRunConfigurationFactory())
}
