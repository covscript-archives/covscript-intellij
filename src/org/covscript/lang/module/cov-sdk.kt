package org.covscript.lang.module

import com.intellij.execution.Platform
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.projectRoots.*
import com.intellij.util.xmlb.XmlSerializer
import com.intellij.util.xmlb.XmlSerializerUtil
import org.covscript.lang.*
import org.jdom.Element


class CovSdkType : SdkType(COV_NAME) {
	override fun getPresentableName() = COV_NAME
	override fun getIcon() = COV_BIG_ICON
	override fun getIconForAddAction() = icon
	override fun isValidSdkHome(s: String?) = validateCovSDK(s.orEmpty())
	override fun suggestSdkName(p0: String?, p1: String?) = ""
	override fun suggestHomePath() = if (Platform.current() == Platform.WINDOWS) POSSIBLE_SDK_HOME_WINDOWS else POSSIBLE_SDK_HOME_LINUX
	override fun createAdditionalDataConfigurable(model: SdkModel, modificator: SdkModificator) = null
	override fun saveAdditionalData(additionalData: SdkAdditionalData, element: Element) {
		if (additionalData is CovSdkData) XmlSerializer.serializeInto(additionalData, element)
	}

	override fun loadAdditionalData(additional: Element): SdkAdditionalData? {
		return XmlSerializer.deserialize(additional, CovSdkData::class.java)
	}

	companion object InstanceHolder {
		val instance get() = SdkType.findInstance(CovSdkType::class.java)
	}
}

class CovSdkData(var covSdkPath: String) : SdkAdditionalData, PersistentStateComponent<CovSdkData> {
	override fun getState() = this
	override fun clone() = CovSdkData(covSdkPath)
	override fun loadState(state: CovSdkData) = XmlSerializerUtil.copyBean(state, this)
}