package org.covscript.lang.execution;

import com.android.annotations.NonNull;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.covscript.lang.module.CovSdkComboBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CovRunConfigurationEditor extends SettingsEditor<CovRunConfiguration> {
	private @NonNull JPanel mainPanel;
	private @NonNull CovSdkComboBox sdkComboBox;
	private @NonNull TextFieldWithBrowseButton covExecutiveField;
	private @NonNull TextFieldWithBrowseButton workingDirField;
	private @NonNull JTextField additionalParamsField;
	private TextFieldWithBrowseButton targetFileField;

	public CovRunConfigurationEditor(@NonNull CovRunConfiguration configuration) {
		sdkComboBox.getComboBox().setSelectedItem(configuration.getSdkUsed());
		covExecutiveField.setText(configuration.getCovExecutive());
		workingDirField.setText(configuration.getWorkingDir());
	}

	@Override protected void resetEditorFrom(@NotNull CovRunConfiguration configuration) {
		additionalParamsField.setText(configuration.getAdditionalParams());
		covExecutiveField.setText(configuration.getCovExecutive());
		targetFileField.setText(configuration.getTargetFile());
		workingDirField.setText(configuration.getWorkingDir());
		sdkComboBox.getComboBox().setSelectedItem(configuration.getSdkUsed());
	}

	@Override protected void applyEditorTo(@NotNull CovRunConfiguration configuration) {
		configuration.setAdditionalParams(additionalParamsField.getText());
		configuration.setCovExecutive(covExecutiveField.getText());
		configuration.setTargetFile(targetFileField.getText());
		configuration.setWorkingDir(workingDirField.getText());
		configuration.setSdkUsed(sdkComboBox.getSelectedSdk());
	}

	@Override protected @NotNull JPanel createEditor() {
		return mainPanel;
	}
}
