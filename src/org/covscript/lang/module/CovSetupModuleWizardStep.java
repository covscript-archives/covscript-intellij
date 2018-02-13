package org.covscript.lang.module;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.labels.LinkLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class CovSetupModuleWizardStep extends ModuleWizardStep {
	protected @NotNull JPanel mainPanel;
	protected @NotNull LinkLabel<Object> covWebsiteLink;
	protected @NotNull JLabel covPathExample;
	protected @NotNull JLabel covWebsiteDescription;
	protected @NotNull TextFieldWithBrowseButton covHomeField;
}
