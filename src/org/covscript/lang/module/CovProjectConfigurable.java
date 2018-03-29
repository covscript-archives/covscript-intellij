package org.covscript.lang.module;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.labels.LinkLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class CovProjectConfigurable implements Configurable {
	protected @NotNull JPanel mainPanel;
	protected @NotNull JFormattedTextField timeLimitField;
	protected @NotNull JFormattedTextField textLimitField;
	protected @NotNull TextFieldWithBrowseButton covExeField;
	protected @NotNull JLabel version;
	protected @NotNull LinkLabel<Object> covWebsite;
	protected @NotNull RawCommandLineEditor importPathField;
}
