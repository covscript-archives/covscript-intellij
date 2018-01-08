package org.covscript.lang.module

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.roots.ModifiableRootModel
import org.covscript.lang.*

class CovModuleBuilder : ModuleBuilder() {
	private val projectWizardData = CovProjectWizardData(System.getenv("").orEmpty())
	override fun getModuleType() = CovModuleType.instance
	override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): CovSetupSdkWizardStep {
		parentDisposable.dispose()
		context.defaultModuleName = COV_DEFAULT_MODULE_NAME
		return CovSetupSdkWizardStep(projectWizardData)
	}

	override fun setupRootModel(model: ModifiableRootModel?) {
		println("Debug breakpoint")
	}
}

class CovModuleType : ModuleType<CovModuleBuilder>(ID) {
	override fun getName() = COV_NAME
	override fun getNodeIcon(bool: Boolean) = COV_BIG_ICON
	override fun createModuleBuilder() = CovModuleBuilder()
	override fun getDescription() = "CovScript Module Type"

	companion object InstanceHolder {
		private const val ID = "COV_MODULE_TYPE"
		@JvmStatic val instance: CovModuleType get() = ModuleTypeManager.getInstance().findByID(ID) as CovModuleType
	}
}

fun validateCovSDK(path: String): Boolean {
	return false
}

class CovProjectWizardData(var covSdkPath: String)
