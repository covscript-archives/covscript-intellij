package org.covscript.lang.module

import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.projectRoots.ui.ProjectJdksEditor
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.util.SystemInfo
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.ComboboxWithBrowseButton
import org.covscript.lang.*
import org.jdom.Element
import javax.swing.DefaultComboBoxModel
import javax.swing.JList

class CovSdkType : SdkType(CovBundle.message("cov.name")) {
	override fun getPresentableName() = CovBundle.message("cov.modules.sdk.name")
	override fun getIcon() = COV_BIG_ICON
	override fun getIconForAddAction() = icon
	override fun isValidSdkHome(s: String?) = validateCovSDK(s.orEmpty())
	override fun suggestSdkName(s: String?, p1: String?) = CovBundle.message("cov.modules.sdk.name")
	override fun suggestHomePath() = if (SystemInfo.isWindows) POSSIBLE_SDK_HOME_WINDOWS else POSSIBLE_SDK_HOME_LINUX
	override fun createAdditionalDataConfigurable(model: SdkModel, modificator: SdkModificator) = null
	override fun getVersionString(sdkHome: String?) = versionOf(sdkHome.orEmpty())
	override fun saveAdditionalData(additionalData: SdkAdditionalData, element: Element) = Unit // leave blank
	override fun getDownloadSdkUrl() = COV_WEBSITE
	override fun setupSdkPaths(sdk: Sdk, sdkModel: SdkModel): Boolean {
		val modificator = sdk.sdkModificator
		modificator.versionString = getVersionString(sdk)
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

fun SdkAdditionalData?.toCovSdkData() = this as? CovSdkData

class CovSdkData : SdkAdditionalData {
	var tryEvaluateTimeLimit = 2500L
	var tryEvaluateTextLimit = 320
	override fun clone() = CovSdkData().also {
		it.tryEvaluateTextLimit = tryEvaluateTextLimit
		it.tryEvaluateTimeLimit = tryEvaluateTimeLimit
	}
}

class CovSdkComboBox : ComboboxWithBrowseButton() {
	val selectedSdk get() = comboBox.selectedItem as? Sdk
	val sdkName get() = selectedSdk?.name.orEmpty()
	val sdkHomePath get() = selectedSdk?.homePath.orEmpty()

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
			editor.title = CovBundle.message("cov.modules.sdk.selection.title")
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
