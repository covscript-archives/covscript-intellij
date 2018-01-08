package org.covscript.lang.module

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.vfs.VirtualFile
import org.covscript.lang.*
import java.nio.file.Files
import java.nio.file.Paths

class CovModuleBuilder : ModuleBuilder() {
	private val projectWizardData = CovProjectWizardData(System.getenv("").orEmpty())
	override fun getModuleType() = CovModuleType.instance
	override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): CovSetupSdkWizardStep {
		parentDisposable.dispose()
		context.defaultModuleName = COV_DEFAULT_MODULE_NAME
		return CovSetupSdkWizardStep(projectWizardData)
	}

	override fun setupRootModel(model: ModifiableRootModel) {
		doAddContentEntry(model)?.file?.let { setupCovModule(model, it, projectWizardData) }
	}

	private fun setupCovModule(model: ModifiableRootModel, file: VirtualFile, data: CovProjectWizardData) {

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

fun validateCovSDK(pathString: String): Boolean {
	val csPath = Paths.get(pathString, "bin", "cs")
	val csReplPath = Paths.get(pathString, "bin", "cs_repl")
	return Files.exists(csPath) and
			Files.isExecutable(csPath) and
			Files.exists(csReplPath) and
			Files.isExecutable(csReplPath)
}

class CovProjectWizardData(var covSdkPath: String)
