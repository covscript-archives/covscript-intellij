package org.covscript.lang.execution;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.covscript.lang.CovBundle;
import org.covscript.lang.CovFileType;
import org.covscript.lang.module.CovSdkComboBox;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Paths;

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
		covExecutiveField.addBrowseFolderListener(CovBundle.message("cov.messages.run.select-interpreter"),
				CovBundle.message("cov.messages.run.select-interpreter.description"),
				null,
				FileChooserDescriptorFactory.createSingleFileDescriptor());
		workingDirField.addBrowseFolderListener(CovBundle.message("cov.messages.run.select-working-dir"),
				CovBundle.message("cov.messages.run.select-working-dir.description"),
				null,
				FileChooserDescriptorFactory.createSingleFolderDescriptor());
		targetFileField.addBrowseFolderListener(CovBundle.message("cov.messages.run.select-cov-file"),
				CovBundle.message("cov.messages.run.select-cov-file.description"),
				null,
				FileChooserDescriptorFactory.createSingleFileDescriptor(CovFileType.INSTANCE));
		resetEditorFrom(configuration);
	}

	@Override protected void resetEditorFrom(@NotNull CovRunConfiguration configuration) {
		logPath.setSelected(configuration.getLogPathOption());
		importPath.setSelected(configuration.getImportPathOption());
		compileOnly.setSelected(configuration.getCompileOnlyOption());
		waitBeforeExit.setSelected(configuration.getWaitB4ExitOption());
		logPathField.setText(configuration.getLogPath());
		logPathField.setEnabled(logPath.isSelected());
		importPathField.setText(configuration.getImportPath());
		importPathField.setEnabled(importPath.isSelected());
		covExecutiveField.setText(configuration.getCovExecutive());
		targetFileField.setText(configuration.getTargetFile());
		workingDirField.setText(configuration.getWorkingDir());
		sdkComboBox.getComboBox().setSelectedItem(configuration.getSdkUsed());
	}

	@Override protected void applyEditorTo(@NotNull CovRunConfiguration configuration) throws ConfigurationException {
		boolean logPathOp = logPath.isSelected();
		configuration.setLogPathOption(logPathOp);
		boolean importPathOp = importPath.isSelected();
		configuration.setImportPathOption(importPathOp);
		configuration.setCompileOnlyOption(compileOnly.isSelected());
		configuration.setWaitB4ExitOption(waitBeforeExit.isSelected());
		if (importPathOp) {
			String importPath = importPathField.getText();
			if (Files.isDirectory(Paths.get(importPath))) configuration.setImportPath(importPath);
			else reportInvalidPath(importPath);
		}
		if (logPathOp) {
			String logPath = logPathField.getText();
			if (Files.isDirectory(Paths.get(logPath).getParent())) configuration.setLogPath(logPath);
			else reportInvalidPath(logPath);
		}
		String covExecutable = covExecutiveField.getText();
		if (Files.isExecutable(Paths.get(covExecutable))) configuration.setCovExecutive(covExecutable);
		else reportInvalidPath(covExecutable);
		String targetFile = targetFileField.getText();
		if (Files.isReadable(Paths.get(targetFile))) configuration.setTargetFile(targetFile);
		else reportInvalidPath(targetFile);
		String workingDirectory = workingDirField.getText();
		if (Files.isDirectory(Paths.get(workingDirectory))) configuration.setWorkingDir(workingDirectory);
		else reportInvalidPath(workingDirectory);
		configuration.setSdkUsed(sdkComboBox.getSelectedSdk());
	}

	@Contract("_ -> fail") private void reportInvalidPath(@NotNull String importPath) throws ConfigurationException {
		throw new ConfigurationException(CovBundle.message("cov.messages.try-eval.invalid-path", importPath));
	}

	@Override protected @NotNull JPanel createEditor() {
		return mainPanel;
	}
}
