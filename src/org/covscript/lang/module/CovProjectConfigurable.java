package org.covscript.lang.module;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class CovProjectConfigurable implements Configurable {
	protected @NotNull JPanel mainPanel;
	protected @NotNull JFormattedTextField timeLimitField;
	protected @NotNull JFormattedTextField textLimitField;
}
