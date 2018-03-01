package org.covscript.lang.execution;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class CovRunConfigurationEditor extends SettingsEditor<CovRunConfiguration> {
	protected @NotNull JPanel mainPanel;
	protected @NotNull TextFieldWithBrowseButton covExecutiveField;
	protected @NotNull TextFieldWithBrowseButton workingDirField;
	protected @NotNull TextFieldWithBrowseButton targetFileField;
	protected @NotNull JCheckBox compileOnly;
	protected @NotNull JCheckBox logPath;
	protected @NotNull JCheckBox importPath;
	protected @NotNull JCheckBox waitBeforeExit;
	protected @NotNull TextFieldWithBrowseButton logPathField;
	protected @NotNull TextFieldWithBrowseButton importPathField;
	protected @NotNull JTextField progArgsField;
}
