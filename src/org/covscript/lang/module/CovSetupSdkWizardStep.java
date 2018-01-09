package org.covscript.lang.module;

import com.android.annotations.NonNull;
import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.labels.LinkLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

import static org.covscript.lang.Cov_constantsKt.JOJO_ICON;
import static org.covscript.lang.module.Cov_modulesKt.validateCovSDK;

public class CovSetupSdkWizardStep extends ModuleWizardStep {
	private @NotNull JPanel mainPanel;
	private @NotNull TextFieldWithBrowseButton sdkPathField;
	private @NotNull LinkLabel<Object> covWebsiteLink;
	private @NotNull JLabel validationInfo;
	private @NonNull JLabel covPathExample;
	private @NonNull JLabel covWebsiteDescription;
	private @NotNull CovProjectWizardData data;

	public CovSetupSdkWizardStep(@NotNull CovProjectWizardData data) {
		this.data = data;
		validationInfo.setVisible(!validateCovSDK(data.getCovSdkPath()));
		covPathExample.setVisible(false);
		covWebsiteLink.setListener((label, o) -> BrowserLauncher.getInstance().open(covWebsiteLink.getText()), null);
		String title = "Select a CovScript SDK", description = "Selecting a CovScript SDK path to use";
		FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
		sdkPathField.setText(data.getCovSdkPath());
		sdkPathField.addBrowseFolderListener(title, description, null, descriptor);
		sdkPathField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
			@Override protected void textChanged(DocumentEvent documentEvent) {
				boolean valid = validateCovSDK(sdkPathField.getText());
				validationInfo.setVisible(!valid);
				covWebsiteDescription.setVisible(!valid);
				covPathExample.setVisible(!valid);
			}
		});
	}

	@Override public JComponent getComponent() {
		return mainPanel;
	}

	@Override public void updateDataModel() {
		if (validationInfo.isVisible()) Messages.showDialog(mainPanel,
				"You're SDK path is invalid,\nyou will not be able to run your code later.",
				"Invalid CovScript SDK",
				new String[]{"Yes! Yes! Yes!"},
				0,
				JOJO_ICON);
		else data.setCovSdkPath(sdkPathField.getText());
	}
}
