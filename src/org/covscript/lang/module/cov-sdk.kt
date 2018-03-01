package org.covscript.lang.module

import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.roots.OrderRootType
import icons.CovIcons
import org.covscript.lang.COV_WEBSITE
import org.covscript.lang.CovBundle
import org.jdom.Element

/**
 * @deprecated
 */
class CovSdkType : SdkType(CovBundle.message("cov.name")) {
	override fun getPresentableName() = CovBundle.message("cov.modules.sdk.name")
	override fun getIcon() = CovIcons.COV_BIG_ICON
	override fun getIconForAddAction() = icon
	override fun isValidSdkHome(s: String?) = validateCovExe(s.orEmpty())
	override fun suggestSdkName(s: String?, p1: String?) = CovBundle.message("cov.modules.sdk.name")
	override fun suggestHomePath() = defaultCovExe
	override fun createAdditionalDataConfigurable(md: SdkModel, m: SdkModificator): AdditionalDataConfigurable? = null
	override fun getVersionString(sdkHome: String?) = versionOf(sdkHome.orEmpty()).first
	override fun saveAdditionalData(additionalData: SdkAdditionalData, element: Element) = Unit // leave blank
	override fun getDownloadSdkUrl() = COV_WEBSITE
	override fun setupSdkPaths(sdk: Sdk, sdkModel: SdkModel): Boolean {
		val modificator = sdk.sdkModificator
		modificator.versionString = getVersionString(sdk) ?: CovBundle.message("cov.modules.sdk.unknown-version")
		sdk.homeDirectory
				?.findChild("imports")
				?.let { modificator.addRoot(it, OrderRootType.CLASSES) }
		modificator.commitChanges()
		return true
	}

	companion object InstanceHolder {
		val instance get() = SdkType.findInstance(CovSdkType::class.java)
	}
}
