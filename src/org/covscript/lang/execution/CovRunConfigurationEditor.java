package org.covscript.lang.execution;

import com.android.annotations.NonNull;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CovRunConfigurationEditor extends SettingsEditor<CovRunConfiguration> {
	private @NonNull JPanel mainPanel;
	private @NonNull CovRunConfiguration configuration;

	public CovRunConfigurationEditor(CovRunConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override protected void resetEditorFrom(@NotNull CovRunConfiguration configuration) {
	}

	@Override protected void applyEditorTo(@NotNull CovRunConfiguration configuration) {

	}

	@Override protected @NotNull JComponent createEditor() {
		return mainPanel;
	}
}
