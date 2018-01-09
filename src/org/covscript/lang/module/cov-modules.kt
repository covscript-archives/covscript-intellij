package org.covscript.lang.module

import com.intellij.facet.FacetManager
import com.intellij.facet.FacetTypeRegistry
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.WizardContext
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

class CovModuleBuilder : ModuleBuilder() {
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
	override fun getWeight() = 100
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
		setupFacet(model.module, sdk)
		ProjectRootManager.getInstance(model.module.project).run {
			projectSdk = sdk
		}
	}

	private fun setupFacet(module: Module, sdk: Sdk) {
		val facetId = CovFacetType.stringId
		if (facetId.isNotBlank()) {
			val facetManager = FacetManager.getInstance(module)
			val type = FacetTypeRegistry.getInstance().findFacetType(facetId) as? CovFacetType ?: return
			if (facetManager.getFacetByType(type.id) == null) {
				val model = facetManager.createModifiableModel()
				val facet = facetManager.addFacet(type, type.defaultFacetName, null) as? CovFacet ?: return
				facet.configuration.data.covSdkName = sdk.name
				model.addFacet(facet)
				model.commit()
			}
		}
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

//class CovModuleProvider : FrameworkSupportInModuleProvider() {
//	override fun getFrameworkType(): CovFrameworkType = FrameworkTypeEx.EP_NAME.findExtension(CovFrameworkType::class.java)
//	override fun isEnabledForModuleType(moduleType: ModuleType<*>) = moduleType is CovModuleType
//	override fun createConfigurable(model: FrameworkSupportModel) = CovModuleConfigurable()
//}

fun validateCovSDK(pathString: String): Boolean {
	val csPath = Paths.get(pathString, "bin", "cs")
	val csReplPath = Paths.get(pathString, "bin", "cs_repl")
	val csExePath = Paths.get(pathString, "bin", "cs.exe")
	val csExeReplPath = Paths.get(pathString, "bin", "cs_repl.exe")
	return (csPath.isExe() || csExePath.isExe()) && (csReplPath.isExe() || csExeReplPath.isExe())
}

fun Path.isExe() = Files.exists(this) and Files.isExecutable(this)
@JvmField val COV_SDK_LIB_KEY = Key<CovSdkData>(COV_SDK_LIB_NAME)
