package org.covscript.lang.module

import com.intellij.facet.*
import com.intellij.facet.ui.FacetEditorContext
import com.intellij.facet.ui.FacetValidatorsManager
import com.intellij.openapi.components.*
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.util.xmlb.XmlSerializerUtil
import org.covscript.lang.COV_BIG_ICON
import org.covscript.lang.COV_NAME
import org.jdom.Element

@State(
		name = "CovFacetConfiguration",
		storages = [Storage(file = "\$MODULE_FILE\$"),
			Storage(id = "dir", file = "\$PROJECT_CONFIG_DIR\$/cov_config.xml", scheme = StorageScheme.DIRECTORY_BASED)])
class CovFacetConfiguration : FacetConfiguration, PersistentStateComponent<CovSdkData> {
	val data = CovSdkData("")
	@Suppress("OverridingDeprecatedMember") override fun readExternal(element: Element?) = Unit
	@Suppress("OverridingDeprecatedMember") override fun writeExternal(element: Element?) = Unit
	override fun getState() = data
	override fun createEditorTabs(context: FacetEditorContext?, manager: FacetValidatorsManager?) = arrayOf(CovFacetEditorTab(this))
	override fun loadState(sdkData: CovSdkData?) {
		sdkData?.let { XmlSerializerUtil.copyBean(it, data) }
	}
}

object CovFacetType : FacetType<CovFacet, CovFacetConfiguration>(CovFacet.COV_FACET_ID, COV_NAME, COV_NAME) {
	override fun createDefaultConfiguration() = CovFacetConfiguration()
	override fun getIcon() = COV_BIG_ICON
	override fun isSuitableModuleType(type: ModuleType<*>?) = type is CovModuleType
	override fun createFacet(module: Module, s: String?, configuration: CovFacetConfiguration, facet: Facet<*>?) =
			CovFacet(this, module, configuration, facet)
}

class CovFacet(
		facetType: FacetType<CovFacet, CovFacetConfiguration>,
		module: Module,
		configuration: CovFacetConfiguration,
		underlyingFacet: Facet<*>?) : Facet<CovFacetConfiguration>(facetType, module, COV_NAME, configuration, underlyingFacet) {
	constructor(module: Module) : this(FacetTypeRegistry.getInstance().findFacetType(COV_FACET_ID), module, CovFacetConfiguration(), null)

	companion object InstanceHolder {
		@JvmField val COV_FACET_ID = FacetTypeId<CovFacet>(COV_NAME)
		fun getInstance(module: Module) = FacetManager.getInstance(module).getFacetByType(COV_FACET_ID)
	}
}

//class CovFrameworkType : FrameworkTypeEx(COV_FW_ID) {
//	override fun getIcon() = COV_BIG_ICON
//	override fun getPresentableName() = COV_SDK_NAME
//	override fun createProvider() = CovModuleProvider()
//
//	companion object IdHolder {
//		private val COV_FW_ID = "COV_FRAMEWORK_ID"
//	}
//}
