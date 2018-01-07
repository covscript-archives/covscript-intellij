package org.covscript.lang.module

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import org.covscript.lang.COV_BIG_ICON
import org.covscript.lang.COV_NAME

class CovModuleBuilder : ModuleBuilder() {
	override fun getModuleType() = CovModuleType.instance
	override fun setupRootModel(model: ModifiableRootModel?) {
	}
}

class CovModuleType : ModuleType<CovModuleBuilder>(ID) {
	override fun getName() = COV_NAME
	override fun getNodeIcon(bool: Boolean) = COV_BIG_ICON
	override fun createModuleBuilder() = CovModuleBuilder()
	override fun getDescription() = "CovScript Module Type"
	override fun createWizardSteps(wizardContext: WizardContext, moduleBuilder: CovModuleBuilder, modulesProvider: ModulesProvider) =
			arrayOf(CovSetupSdkWizardStep())

	companion object InstanceHolder {
		private const val ID = "COV_MODULE_TYPE"
		@JvmStatic val instance: CovModuleType get() = ModuleTypeManager.getInstance().findByID(ID) as CovModuleType
	}
}
