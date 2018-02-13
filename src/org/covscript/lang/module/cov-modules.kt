package org.covscript.lang.module

import com.google.common.util.concurrent.SimpleTimeLimiter
import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ProjectRootManager
import icons.CovIcons
import org.covscript.lang.*
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

class CovModuleBuilder : ModuleBuilder() {
	lateinit var settings: CovSettings
	override fun isSuitableSdkType(sdkType: SdkTypeId?) = sdkType is CovSdkType
	override fun getWeight() = 99
	override fun getNodeIcon() = CovIcons.COV_BIG_ICON
	override fun getModuleType() = CovModuleType.instance
	override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): CovSetupSdkWizardStep {
		parentDisposable.dispose()
		context.projectName = COV_DEFAULT_MODULE_NAME
		return CovSetupSdkWizardStepImpl(this)
	}

	override fun setupRootModel(model: ModifiableRootModel) {
		model.inheritSdk()
		Files.createDirectories(Paths.get(contentEntryPath, "src"))
		doAddContentEntry(model)
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
		var covHome: String = "",
		var tryEvaluateTimeLimit: Long = 2500L,
		var tryEvaluateTextLimit: Int = 320)
