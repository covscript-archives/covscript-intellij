package org.covscript.lang.module

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import org.covscript.lang.*
import java.nio.file.*

class CovModuleBuilder : ModuleBuilder() {
	private val projectWizardData = CovProjectWizardData(System.getenv(COV_SDK_HOME_KEY).orEmpty())
	override fun getModuleType() = CovModuleType.instance
	override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): CovSetupSdkWizardStep {
		parentDisposable.dispose()
		context.projectName = COV_DEFAULT_MODULE_NAME
		return CovSetupSdkWizardStep(projectWizardData)
	}

	override fun setupRootModel(model: ModifiableRootModel) {
		doAddContentEntry(model)?.file?.let { setupCovModule(model, it, projectWizardData) }
	}

	private fun setupCovModule(model: ModifiableRootModel, basePath: VirtualFile, data: CovProjectWizardData) {
		basePath.createChildDirectory(this, "src")
		LocalFileSystem.getInstance().refreshAndFindFileByPath(data.covSdkPath)
		model.project.putUserData(COV_SDK_LIB_KEY, data.covSdkPath)
	}
}

class CovModuleType : ModuleType<CovModuleBuilder>(ID) {
	override fun getName() = COV_NAME
	override fun getNodeIcon(bool: Boolean) = COV_BIG_ICON
	override fun createModuleBuilder() = CovModuleBuilder()
	override fun getDescription() = COV_MODULE_TYPE_DESCRIPTION

	companion object InstanceHolder {
		private const val ID = "COV_MODULE_TYPE"
		@JvmStatic val instance: CovModuleType get() = ModuleTypeManager.getInstance().findByID(ID) as CovModuleType
	}
}

fun validateCovSDK(pathString: String): Boolean {
	val csPath = Paths.get(pathString, "bin", "cs")
	val csReplPath = Paths.get(pathString, "bin", "cs_repl")
	val csExePath = Paths.get(pathString, "bin", "cs.exe")
	val csExeReplPath = Paths.get(pathString, "bin", "cs_repl.exe")
	return (csPath.isExe() || csExePath.isExe()) && (csReplPath.isExe() || csExeReplPath.isExe())
}

fun Path.isExe() = Files.exists(this) and Files.isExecutable(this)

class CovProjectWizardData(var covSdkPath: String)
