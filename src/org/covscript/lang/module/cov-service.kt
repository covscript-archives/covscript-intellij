package org.covscript.lang.module

import com.intellij.openapi.components.*
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

interface CovProjectSettingsService {
	val settings: CovSettings
}

val Project.covSettings: CovProjectSettingsService get() = covSettingsNullable!!
val Project.covSettingsNullable: CovProjectSettingsService?
	get() = getService(this, CovProjectSettingsService::class.java)

@State(
		name = "CovProjectSettings",
		storages = [Storage(file = "covscriptConfig.xml")])
class CovProjectSettingsServiceImpl :
		CovProjectSettingsService, PersistentStateComponent<CovSettings> {
	override val settings = CovSettings()
	override fun getState(): CovSettings? = XmlSerializerUtil.createCopy(settings)
	override fun loadState(state: CovSettings) {
		XmlSerializerUtil.copyBean(state, settings)
	}
}
