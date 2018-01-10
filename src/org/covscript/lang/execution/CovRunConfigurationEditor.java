package org.covscript.lang.execution;

import com.android.annotations.NonNull;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.covscript.lang.CovFileType;
import org.covscript.lang.module.CovSdkComboBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CovRunConfigurationEditor extends SettingsEditor<CovRunConfiguration> {
	private @NonNull JPanel mainPanel;
	private @NonNull CovSdkComboBox sdkComboBox;
	private @NonNull TextFieldWithBrowseButton covExecutiveField;
	private @NonNull TextFieldWithBrowseButton workingDirField;
	private @NonNull JTextField additionalParamsField;
	private @NonNull TextFieldWithBrowseButton targetFileField;

	public CovRunConfigurationEditor(@NonNull CovRunConfiguration configuration) {
		sdkComboBox.getComboBox().setSelectedItem(configuration.getSdkUsed());
		sdkComboBox.addActionListener(actionEvent -> covExecutiveField.setText(sdkComboBox.getSdkHomePath()));
		covExecutiveField.setText(configuration.getCovExecutive());
		String exeTitle = "Select a CovScript Interpreter";
		String exeDescription = exeTitle + " to execute your script";
		FileChooserDescriptor exeDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
		covExecutiveField.addBrowseFolderListener(exeTitle, exeDescription, null, exeDescriptor);
		workingDirField.setText(configuration.getWorkingDir());
		String workTitle = "Select a Working Directory";
		String workDescription = workTitle + " for execution";
		FileChooserDescriptor workDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
		workingDirField.addBrowseFolderListener(workTitle, workDescription, null, workDescriptor);
		String scriptTitle = "Select a CovScript File";
		String scriptDescription = scriptTitle + " to execute";
		FileChooserDescriptor scriptDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(CovFileType.INSTANCE);
		targetFileField.addBrowseFolderListener(scriptTitle, scriptDescription, null, scriptDescriptor);
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
