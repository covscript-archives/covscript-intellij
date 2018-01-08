package org.covscript.lang.module;

import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.labels.LinkLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CovSetupSdkWizardStep extends ModuleWizardStep {
	private @NotNull JPanel mainPanel;
	private @NotNull TextFieldWithBrowseButton sdkPathField;
	private @NotNull LinkLabel<Object> covWebsiteLink;

	public CovSetupSdkWizardStep() {
		covWebsiteLink.setListener((label, o) -> BrowserLauncher.getInstance().open(covWebsiteLink.getText()), null);
		String title = "Select a CovScript SDK", description = "Selecting a CovScript SDK path to use";
		FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
		sdkPathField.addBrowseFolderListener(title, description, null, descriptor);
	}

	@Override public JComponent getComponent() {
		return mainPanel;
	}

	@Override public void updateDataModel() {
	}
}
