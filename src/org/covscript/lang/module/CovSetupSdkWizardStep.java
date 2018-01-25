package org.covscript.lang.module;

import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.labels.LinkLabel;
import org.covscript.lang.CovBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Objects;

public class CovSetupSdkWizardStep extends ModuleWizardStep {
	private @NotNull JPanel mainPanel;
	private @NotNull CovSdkComboBox sdkPathField;
	private @NotNull LinkLabel<Object> covWebsiteLink;
	private @NotNull JLabel covPathExample;
	private @NotNull JLabel covWebsiteDescription;
	private @NotNull CovModuleBuilder builder;

	public CovSetupSdkWizardStep(@NotNull CovModuleBuilder builder) {
		this.builder = builder;
		covPathExample.setVisible(false);
		covWebsiteLink.setListener((label, o) -> BrowserLauncher.getInstance().open(covWebsiteLink.getText()), null);
	}

	@Override public boolean validate() throws ConfigurationException {
		if (StringUtil.isEmpty(sdkPathField.getSdkName())) {
			covWebsiteDescription.setVisible(true);
			throw new ConfigurationException(CovBundle.message("cov.modules.sdk.invalid"));
		}
		covWebsiteDescription.setVisible(false);
		return super.validate();
	}

	@Override public JComponent getComponent() {
		return mainPanel;
	}

	@Override public void updateDataModel() {
		builder.setSdk(Objects.requireNonNull(sdkPathField.getSelectedSdk()));
	}
}
