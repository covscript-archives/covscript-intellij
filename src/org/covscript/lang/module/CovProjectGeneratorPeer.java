package org.covscript.lang.module;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.platform.ProjectGeneratorPeer;
import com.intellij.ui.components.labels.LinkLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class CovProjectGeneratorPeer implements ProjectGeneratorPeer<CovSettings> {
	protected @NotNull JPanel mainPanel;
	protected @NotNull LinkLabel<Object> covWebsiteLink;
	protected @NotNull JLabel covWebsiteDescription;
	protected @NotNull JLabel covPathExample;
	protected @NotNull TextFieldWithBrowseButton covHomeField;
}
