package org.covscript.lang.execution;

import com.android.annotations.NonNull;
import com.intellij.openapi.options.SettingsEditor;
import org.covscript.lang.module.CovSdkComboBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CovRunConfigurationEditor extends SettingsEditor<CovRunConfiguration> {
	private @NonNull JPanel mainPanel;
	private @NonNull CovSdkComboBox sdkComboBox;
	private @NonNull CovRunConfiguration configuration;

	public CovRunConfigurationEditor(CovRunConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override protected void resetEditorFrom(@NotNull CovRunConfiguration configuration) {
		this.configuration.setAdditionalParams(configuration.getAdditionalParams());
		this.configuration.setCovExecutive(configuration.getCovExecutive());
		this.configuration.setTargetFile(configuration.getTargetFile());
		this.configuration.setWorkingDir(configuration.getWorkingDir());
	}

	@Override protected void applyEditorTo(@NotNull CovRunConfiguration configuration) {
		configuration.setAdditionalParams(this.configuration.getAdditionalParams());
		configuration.setCovExecutive(this.configuration.getCovExecutive());
		configuration.setTargetFile(this.configuration.getTargetFile());
		configuration.setWorkingDir(this.configuration.getWorkingDir());
	}

	@Override protected @NotNull JComponent createEditor() {
		return mainPanel;
	}
}
