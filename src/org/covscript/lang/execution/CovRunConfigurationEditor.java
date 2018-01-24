package org.covscript.lang.execution;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.covscript.lang.CovBundle;
import org.covscript.lang.CovFileType;
import org.covscript.lang.module.CovSdkComboBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CovRunConfigurationEditor extends SettingsEditor<CovRunConfiguration> {
	private @NotNull JPanel mainPanel;
	private @NotNull CovSdkComboBox sdkComboBox;
	private @NotNull TextFieldWithBrowseButton covExecutiveField;
	private @NotNull TextFieldWithBrowseButton workingDirField;
	private @NotNull TextFieldWithBrowseButton targetFileField;
	private @NotNull JCheckBox compileOnly;
	private @NotNull JCheckBox logPath;
	private @NotNull JCheckBox importPath;
	private @NotNull JCheckBox waitBeforeExit;
	private @NotNull TextFieldWithBrowseButton logPathField;
	private @NotNull TextFieldWithBrowseButton importPathField;

	public CovRunConfigurationEditor(@NotNull CovRunConfiguration configuration) {
		sdkComboBox.addPropertyChangeListener(changeEvent -> {
			configuration.setSdkUsed(sdkComboBox.getSelectedSdk());
			covExecutiveField.setText(configuration.getCovExecutive());
		});
		logPath.addChangeListener(actionEvent -> logPathField.setEnabled(logPath.isSelected()));
		importPath.addChangeListener(actionEvent -> importPathField.setEnabled(importPath.isSelected()));
		String exeTitle = CovBundle.message("cov.messages.run.select-interpreter");
		String exeDescription = CovBundle.message("cov.messages.run.select-interpreter.description");
		FileChooserDescriptor exeDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
		covExecutiveField.addBrowseFolderListener(exeTitle, exeDescription, null, exeDescriptor);
		String workTitle = CovBundle.message("cov.messages.run.select-working-dir");
		String workDescription = CovBundle.message("cov.messages.run.select-working-dir.description");
		FileChooserDescriptor workDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
		workingDirField.addBrowseFolderListener(workTitle, workDescription, null, workDescriptor);
		String scriptTitle = CovBundle.message("cov.messages.run.select-cov-file");
		String scriptDescription = CovBundle.message("cov.messages.run.select-cov-file.description");
		FileChooserDescriptor scriptDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(CovFileType.INSTANCE);
		targetFileField.addBrowseFolderListener(scriptTitle, scriptDescription, null, scriptDescriptor);
		resetEditorFrom(configuration);
	}

	@Override protected void resetEditorFrom(@NotNull CovRunConfiguration configuration) {
		logPath.setSelected(configuration.getLogPathOption());
		importPath.setSelected(configuration.getImportPathOption());
		compileOnly.setSelected(configuration.getCompileOnlyOption());
		waitBeforeExit.setSelected(configuration.getWaitB4ExitOption());
		logPathField.setText(configuration.getLogPath());
		importPathField.setText(configuration.getImportPath());
		covExecutiveField.setText(configuration.getCovExecutive());
		targetFileField.setText(configuration.getTargetFile());
		workingDirField.setText(configuration.getWorkingDir());
		sdkComboBox.getComboBox().setSelectedItem(configuration.getSdkUsed());
	}

	@Override protected void applyEditorTo(@NotNull CovRunConfiguration configuration) throws ConfigurationException {
		configuration.setLogPathOption(logPath.isSelected());
		configuration.setImportPathOption(importPath.isSelected());
		configuration.setCompileOnlyOption(compileOnly.isSelected());
		configuration.setWaitB4ExitOption(waitBeforeExit.isSelected());
		configuration.setImportPath(importPathField.getText());
		configuration.setLogPath(logPathField.getText());
		configuration.setCovExecutive(covExecutiveField.getText());
		configuration.setTargetFile(targetFileField.getText());
		configuration.setWorkingDir(workingDirField.getText());
		configuration.setSdkUsed(sdkComboBox.getSelectedSdk());
	}

	@Override protected @NotNull JPanel createEditor() {
		return mainPanel;
	}
}
