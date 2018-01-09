package org.covscript.lang.module;

import com.android.annotations.NonNull;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static org.covscript.lang.Cov_constantsKt.COV_NAME;

public class CovFacetEditorTab extends FacetEditorTab {
	private @NonNull JPanel mainPanel;
	private @NonNull CovSdkComboBox covComboBox;
	private @NonNull CovFacetConfiguration configuration;

	public CovFacetEditorTab(@NonNull CovFacetConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override public @NotNull JPanel createComponent() {
		return mainPanel;
	}

	@Override public @Nls @NonNull String getDisplayName() {
		return COV_NAME;
	}

	@Override public void apply() {
		configuration.getData().setCovSdkName(covComboBox.getSdkName());
	}

	@Override public void reset() {
		covComboBox.getComboBox()
				.setSelectedItem(ProjectJdkTable.getInstance().findJdk(configuration.getData().getCovSdkName()));
	}

	@Override public boolean isModified() {
		return !covComboBox.getSdkName().equals(configuration.getData().getCovSdkName());
	}
}
