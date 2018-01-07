package org.covscript.lang.module;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.ui.components.labels.LinkLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CovSetupSdkWizardStep extends ModuleWizardStep {
	private @NotNull JPanel mainPanel;
	private TextFieldWithHistoryWithBrowseButton sdkPathField;
	private LinkLabel covWebsiteLabel;

	@Override public JComponent getComponent() {
		return mainPanel;
	}

	@Override public void updateDataModel() {
	}
}
