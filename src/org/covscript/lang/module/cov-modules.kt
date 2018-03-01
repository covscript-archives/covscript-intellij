package org.covscript.lang.module

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.PlatformUtils
import icons.CovIcons
import org.covscript.lang.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class CovModuleBuilder : ModuleBuilder() {
	lateinit var settings: CovSettings
	override fun isSuitableSdkType(sdkType: SdkTypeId?) = sdkType is CovSdkType
	override fun getWeight() = 99
	override fun getNodeIcon() = CovIcons.COV_BIG_ICON
	override fun getModuleType() = CovModuleType.instance
	override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): CovSetupModuleWizardStep {
		parentDisposable.dispose()
		context.projectName = COV_DEFAULT_MODULE_NAME
		return CovSetupModuleWizardStepImpl(this)
	}

	override fun setupRootModel(model: ModifiableRootModel) {
		val project = model.module.project
		if (::settings.isInitialized) (project.covSettings as CovProjectSettingsServiceImpl)
				.loadState(settings)
		model.inheritSdk()
		val srcPath = Paths.get(contentEntryPath, "src").toAbsolutePath()
		Files.createDirectories(srcPath)
		//Idea Only
		if (PlatformUtils.isIntelliJ()) {
			val sourceRoot = project
					.baseDir
					.fileSystem
					.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(srcPath.toString()))
					?: return
			doAddContentEntry(model)?.addSourceFolder(sourceRoot, false)
		} else {
			//other Platform just doAddContentEntry
			doAddContentEntry(model)
		}
	}
}

class CovModuleType : ModuleType<CovModuleBuilder>(COV_MODULE_ID) {
	override fun getName() = CovBundle.message("cov.name")
	override fun getNodeIcon(bool: Boolean) = CovIcons.COV_BIG_ICON
	override fun createModuleBuilder() = CovModuleBuilder()
	override fun getDescription() = CovBundle.message("cov.modules.type")

	companion object InstanceHolder {
		@JvmStatic val instance: CovModuleType get() = ModuleTypeManager.getInstance().findByID(COV_MODULE_ID) as CovModuleType
	}
}

class CovSettings(
		var exePath: String = "",
		var importPaths: String = "",
		var version: String = "",
		var tryEvaluateTimeLimit: Long = 2500L,
		var tryEvaluateTextLimit: Int = 320) {
	fun initWithHome() {
		val (newVersion, newImport) = versionOf(exePath)
		version = newVersion
		importPaths = newImport
	}

	val imports get() = importPaths.split(File.pathSeparatorChar)
}
