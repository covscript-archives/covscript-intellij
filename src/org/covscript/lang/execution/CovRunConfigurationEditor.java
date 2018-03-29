package org.covscript.lang.execution;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.JBCheckBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class CovRunConfigurationEditor extends SettingsEditor<CovRunConfiguration> {
	protected @NotNull JPanel mainPanel;
	protected @NotNull TextFieldWithBrowseButton covExecutiveField;
	protected @NotNull TextFieldWithBrowseButton workingDirField;
	protected @NotNull TextFieldWithBrowseButton targetFileField;
	protected @NotNull JBCheckBox compileOnly;
	protected @NotNull JBCheckBox logPath;
	protected @NotNull JBCheckBox importPath;
	protected @NotNull JBCheckBox waitBeforeExit;
	protected @NotNull TextFieldWithBrowseButton logPathField;
	protected @NotNull RawCommandLineEditor importPathField;
	protected @NotNull RawCommandLineEditor progArgsField;
}
