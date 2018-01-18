package org.covscript.lang.module;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

import static org.covscript.lang.module.Cov_sdkKt.toCovSdkData;

public class CovAdditionalDataConfigurable implements AdditionalDataConfigurable {
	private @NotNull JPanel mainPanel;
	private @NotNull JFormattedTextField textLimitField;
	private @NotNull JFormattedTextField timeLimitField;
	private @Nullable Sdk sdk;

	public CovAdditionalDataConfigurable() {
		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setGroupingUsed(false);
		DefaultFormatterFactory factory = new DefaultFormatterFactory(new NumberFormatter(format));
		timeLimitField.setFormatterFactory(factory);
		textLimitField.setFormatterFactory(factory);

	}

	@Override public void setSdk(@NotNull Sdk sdk) {
		this.sdk = sdk;
		CovSdkData data = toCovSdkData(sdk.getSdkAdditionalData());
		if (data == null) return;
		timeLimitField.setValue(data.getTryEvaluateTimeLimit());
		textLimitField.setValue(data.getTryEvaluateTextLimit());
	}

	@Override public @NotNull JComponent createComponent() {
		return mainPanel;
	}

	@Override public boolean isModified() {
		return false;
	}

	@Override public void apply() throws ConfigurationException {

	}
}
