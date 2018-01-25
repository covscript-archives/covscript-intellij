package org.covscript.lang.module;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import org.covscript.lang.CovBundle;
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
		if (sdk == null) return false;
		CovSdkData data = toCovSdkData(sdk.getSdkAdditionalData());
		return data == null ||
				!textLimitField.getValue().equals(Integer.valueOf(data.getTryEvaluateTextLimit()).longValue()) ||
				!timeLimitField.getValue().equals(data.getTryEvaluateTimeLimit());
	}

	@Override public void reset() {
		if (sdk == null) return;
		CovSdkData data = toCovSdkData(sdk.getSdkAdditionalData());
		if (data == null) return;
		timeLimitField.setValue(data.getTryEvaluateTimeLimit());
		textLimitField.setValue(data.getTryEvaluateTextLimit());
	}

	@Override public @NotNull String getTabName() {
		return CovBundle.message("cov.modules.sdk.try-eval.title");
	}

	@Override public void apply() throws ConfigurationException {
		if (sdk == null) throw new ConfigurationException("Sdk is null!");
		SdkModificator modificator = sdk.getSdkModificator();
		Object timeLimitFieldValue = timeLimitField.getValue();
		Object textLimitFieldValue = textLimitField.getValue();
		if (!(timeLimitFieldValue instanceof Number && textLimitFieldValue instanceof Number)) return;
		modificator.setSdkAdditionalData(new CovSdkData(((Number) timeLimitFieldValue).longValue(),
				((Number) textLimitFieldValue).intValue()));
		modificator.commitChanges();
	}
}
