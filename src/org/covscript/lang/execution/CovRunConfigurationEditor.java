package org.covscript.lang.execution;

import com.android.annotations.NonNull;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CovRunConfigurationEditor extends SettingsEditor<CovRunConfiguration> {
	private @NonNull JPanel mainPanel;

	@Override protected void resetEditorFrom(@NotNull CovRunConfiguration covRunConfiguration) {
	}

	@Override protected void applyEditorTo(@NotNull CovRunConfiguration covRunConfiguration) {

	}

	@Override protected @NotNull JComponent createEditor() {
		return mainPanel;
	}
}
