package org.covscript.lang.module

import com.intellij.execution.Platform
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.projectRoots.ui.ProjectJdksEditor
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.ComboboxWithBrowseButton
import org.covscript.lang.*
import org.jdom.Element
import javax.swing.DefaultComboBoxModel
import javax.swing.JList

class CovSdkType : SdkType(COV_NAME) {
	override fun getPresentableName() = COV_SDK_NAME
	override fun getIcon() = COV_BIG_ICON
	override fun getIconForAddAction() = icon
	override fun isValidSdkHome(s: String?) = validateCovSDK(s.orEmpty())
	override fun suggestSdkName(s: String?, p1: String?) = COV_SDK_NAME
	override fun suggestHomePath() = if (Platform.current() == Platform.WINDOWS) POSSIBLE_SDK_HOME_WINDOWS else POSSIBLE_SDK_HOME_LINUX
	override fun createAdditionalDataConfigurable(model: SdkModel, modificator: SdkModificator) = null
	override fun getVersionString(sdk: Sdk) = "Stable"
	override fun getVersionString(sdkHome: String?) = "Stable"
	override fun saveAdditionalData(additionalData: SdkAdditionalData, element: Element) = Unit // leave blank
	override fun setupSdkPaths(sdk: Sdk, sdkModel: SdkModel): Boolean {
		val sdkModificator = sdk.sdkModificator
		sdkModificator.versionString = getVersionString(sdk)
		// addAddOnPackageSources(sdkModificator, homePath)
		sdkModificator.commitChanges()
		return true
	}

	companion object InstanceHolder {
		val instance get() = SdkType.findInstance(CovSdkType::class.java)
	}
}

class CovSdkComboBox : ComboboxWithBrowseButton() {
	val selectedSdk get() = comboBox.selectedItem as? Sdk
	val sdkName get() = selectedSdk?.name.orEmpty()

	init {
		comboBox.setRenderer(object : ColoredListCellRenderer<Sdk?>() {
			override fun customizeCellRenderer(
					list: JList<out Sdk?>,
					value: Sdk?,
					index: Int,
					selected: Boolean,
					hasFocus: Boolean) {
				value?.name?.let(::append)
			}
		})
		addActionListener {
			var selectedSdk = selectedSdk
			val project = ProjectManager.getInstance().defaultProject
			val editor = ProjectJdksEditor(selectedSdk, project, this@CovSdkComboBox)
			editor.title = "Select a CovScript SDK"
			editor.show()
			if (editor.isOK) {
				selectedSdk = editor.selectedJdk
				updateSdkList(selectedSdk)
			}
		}
		updateSdkList()
	}

	private fun updateSdkList(sdkToSelectOuter: Sdk? = null) {
		ProjectJdkTable.getInstance().getSdksOfType(CovSdkType.instance).run {
			comboBox.model = DefaultComboBoxModel(toTypedArray())
			comboBox.selectedItem = sdkToSelectOuter ?: firstOrNull()
		}
	}
}
