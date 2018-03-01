package org.covscript.lang.module

import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.ide.util.PropertiesComponent
import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.platform.ProjectGeneratorPeer
import com.intellij.ui.DocumentAdapter
import org.covscript.lang.COV_SDK_HOME_ID
import org.covscript.lang.CovBundle
import java.text.NumberFormat
import javax.swing.event.DocumentEvent
import javax.swing.text.DefaultFormatterFactory
import javax.swing.text.NumberFormatter

class CovSetupModuleWizardStepImpl(private val builder: CovModuleBuilder) : CovSetupModuleWizardStep() {
	init {
		covWebsiteDescription.isVisible = false
		covWebsiteLink.setListener({ _, _ -> BrowserLauncher.instance.open(covWebsiteLink.text) }, null)
		covExeField.addBrowseFolderListener(TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor()))
		covExeField.text = defaultCovExe
	}

	@Throws(ConfigurationException::class)
	override fun validate(): Boolean {
		if (!validateCovExe(covExeField.text)) {
			covWebsiteDescription.isVisible = true
			throw ConfigurationException(CovBundle.message("cov.project.invalid"))
		}
		covWebsiteDescription.isVisible = false
		return super.validate()
	}

	override fun getComponent() = mainPanel
	override fun updateDataModel() {
		val settings = CovSettings(covExeField.text)
		settings.initWithHome()
		builder.settings = settings
	}
}

class CovProjectGeneratorPeerImpl(private val settings: CovSettings) : CovProjectGeneratorPeer() {
	init {
		covWebsiteDescription.isVisible = false
		covWebsiteLink.setListener({ _, _ -> BrowserLauncher.instance.open(covWebsiteLink.text) }, null)
		covExeField.addBrowseFolderListener(TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor()))
		covExeField.text = settings.exePath
	}

	override fun getSettings() = settings
	override fun buildUI(settingsStep: SettingsStep) = settingsStep.addExpertPanel(component)
	override fun isBackgroundJobRunning() = false
	override fun addSettingsListener(listener: ProjectGeneratorPeer.SettingsListener) = Unit
	/** Deprecated in 2017.3 But We must override it. */
	@Deprecated("", ReplaceWith("addSettingsListener"))
	override fun addSettingsStateListener(
			@Suppress("DEPRECATION")
			listener: com.intellij.platform.WebProjectGenerator.SettingsStateListener) = Unit

	override fun getComponent() = mainPanel
	override fun validate(): ValidationInfo? {
		settings.exePath = covExeField.text
		settings.initWithHome()
		val validate = validateCovExe(settings)
		if (validate) PropertiesComponent.getInstance().setValue(COV_SDK_HOME_ID, covExeField.text)
		else covWebsiteDescription.isVisible = true
		return if (validate) null else ValidationInfo(CovBundle.message("cov.project.invalid"))
	}
}

class CovProjectConfigurableImpl(project: Project) : CovProjectConfigurable() {
	private var settings = project.covSettings.settings
	override fun createComponent() = mainPanel
	override fun getDisplayName() = CovBundle.message("cov.name")
	override fun isModified() = settings.exePath != covExeField.text ||
			settings.tryEvaluateTextLimit != (textLimitField.value as Number).toInt() ||
			settings.tryEvaluateTimeLimit != (timeLimitField.value as Number).toLong()

	@Throws(ConfigurationException::class)
	override fun apply() {
		settings.tryEvaluateTextLimit = (textLimitField.value as? Number
				?: throw ConfigurationException(CovBundle.message("cov.modules.try-eval.invalid"))).toInt()
		settings.tryEvaluateTimeLimit = (timeLimitField.value as? Number
				?: throw ConfigurationException(CovBundle.message("cov.modules.try-eval.invalid"))).toLong()
		if (!validateCovExe(covExeField.text)) throw ConfigurationException(CovBundle.message("cov.project.invalid"))
		PropertiesComponent.getInstance().setValue(COV_SDK_HOME_ID, covExeField.text)
		settings.exePath = covExeField.text
		settings.version = version.text
	}

	init {
		version.text = settings.version
		val factory = DefaultFormatterFactory(NumberFormatter(NumberFormat
				.getIntegerInstance()
				.apply { isGroupingUsed = false }))
		timeLimitField.formatterFactory = factory
		timeLimitField.value = settings.tryEvaluateTimeLimit
		textLimitField.formatterFactory = factory
		textLimitField.value = settings.tryEvaluateTextLimit.toLong()
		covWebsite.setListener({ _, _ -> BrowserLauncher.instance.open(covWebsite.text) }, null)
		covExeField.addBrowseFolderListener(TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor(), project))
		covExeField.text = settings.exePath
		covExeField.textField.document.addDocumentListener(object : DocumentAdapter() {
			override fun textChanged(e: DocumentEvent) {
				val exePath = covExeField.text
				val (newVersion, newImport) = versionOf(exePath)
				version.text = newVersion
				// TODO
				// ``importPaths`` = newImport
			}
		})
	}
}
