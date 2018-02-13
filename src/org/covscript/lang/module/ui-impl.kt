package org.covscript.lang.module

import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.openapi.options.ConfigurationException
import org.covscript.lang.CovBundle

class CovSetupSdkWizardStepImpl(private val builder: CovModuleBuilder) : CovSetupSdkWizardStep() {
	init {
		covWebsiteDescription.isVisible = false
		covWebsiteLink.setListener({ _, _ -> BrowserLauncher.instance.open(covWebsiteLink.text) }, null)
	}

	@Throws(ConfigurationException::class)
	override fun validate(): Boolean {
		if (validateCovSDK(covHomeField.text)) {
			covWebsiteDescription.isVisible = true
			throw ConfigurationException(CovBundle.message("cov.modules.sdk.invalid"))
		}
		covWebsiteDescription.isVisible = false
		return super.validate()
	}

	override fun getComponent() = mainPanel
	override fun updateDataModel() {
		builder.settings = CovSettings(covHomeField.text)
	}
}

