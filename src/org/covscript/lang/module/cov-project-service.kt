package org.covscript.lang.module

import com.intellij.openapi.components.*
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

interface CovProjectSettingsService {
	var settings: CovSettings
}

val Project.covSettings: CovProjectSettingsService
	get() = getService(this, CovProjectSettingsService::class.java)

@State(
		name = "CovProjectSettings",
		storages = [
			Storage(file = "\$PROJECT_FILE\$"),
			Storage(file = "\$PROJECT_CONFIG_DIR\$/covConfig.xml", scheme = StorageScheme.DIRECTORY_BASED)])
class CovProjectSettingsServiceImpl :
		CovProjectSettingsService, PersistentStateComponent<CovSettings> {
	override var settings = CovSettings()
	override fun getState(): CovSettings? = XmlSerializerUtil.createCopy(settings)
	override fun loadState(state: CovSettings) {
		XmlSerializerUtil.copyBean(state, settings)
	}
}
