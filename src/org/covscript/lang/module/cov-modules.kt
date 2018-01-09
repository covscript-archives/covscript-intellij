package org.covscript.lang.module

import com.intellij.facet.FacetManager
import com.intellij.facet.FacetTypeRegistry
import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.*
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.*
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import org.covscript.lang.*
import java.nio.file.*

class CovModuleBuilder : ModuleBuilder(), ModuleBuilderListener {
	init {
		addListener(this)
	}

	lateinit var sdk: Sdk
	private var sourcePaths = mutableListOf<Pair<String, String>>()
		get() {
			if (field.isEmpty()) {
				val path = Paths.get(contentEntryPath, "src")
				Files.createDirectories(path)
				field.add(Pair.create(path.toAbsolutePath().toString(), ""))
			}
			return field
		}

	override fun isSuitableSdkType(sdkType: SdkTypeId?) = sdkType is CovSdkType
	override fun getWeight() = 99
	override fun getNodeIcon() = COV_BIG_ICON
	override fun getModuleType() = CovModuleType.instance
	override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): CovSetupSdkWizardStep {
		parentDisposable.dispose()
		context.projectName = COV_DEFAULT_MODULE_NAME
		return CovSetupSdkWizardStep(this)
	}

	override fun setupRootModel(model: ModifiableRootModel) {
		model.getModuleExtension(CompilerModuleExtension::class.java)?.isExcludeOutput = true
		model.sdk = sdk
		val contentEntry = doAddContentEntry(model) ?: return
		sourcePaths.forEach { libInfo ->
			Files.createDirectories(Paths.get(libInfo.first))
			val sourceRoot = LocalFileSystem
					.getInstance().
					refreshAndFindFileByPath(FileUtil.toSystemIndependentName(libInfo.first))
					?: return@forEach
			contentEntry.addSourceFolder(sourceRoot, false, libInfo.second)
		}
	}

	override fun moduleCreated(module: Module) {
		ProjectRootManager.getInstance(module.project).projectSdk = sdk
	}

	override fun modifySettingsStep(settingsStep: SettingsStep): ModuleWizardStep? {
		return super.modifySettingsStep(settingsStep)
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
