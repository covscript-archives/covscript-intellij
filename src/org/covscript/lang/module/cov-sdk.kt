package org.covscript.lang.module

import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.util.SystemInfo
import icons.CovIcons
import org.covscript.lang.*
import org.jdom.Element

/**
 * @deprecated
 */
class CovSdkType : SdkType(CovBundle.message("cov.name")) {
	override fun getPresentableName() = CovBundle.message("cov.modules.sdk.name")
	override fun getIcon() = CovIcons.COV_BIG_ICON
	override fun getIconForAddAction() = icon
	override fun isValidSdkHome(s: String?) = validateCovHome(s.orEmpty())
	override fun suggestSdkName(s: String?, p1: String?) = CovBundle.message("cov.modules.sdk.name")
	override fun suggestHomePath() = if (SystemInfo.isWindows) POSSIBLE_SDK_HOME_WINDOWS else POSSIBLE_SDK_HOME_LINUX
	override fun createAdditionalDataConfigurable(md: SdkModel, m: SdkModificator): AdditionalDataConfigurable? = null
	override fun getVersionString(sdkHome: String?) = versionOf(sdkHome.orEmpty())
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
