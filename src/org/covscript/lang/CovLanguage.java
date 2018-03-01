package org.covscript.lang;

import com.intellij.lang.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static org.covscript.lang.Cov_constantsKt.COV_EXTENSION;
import static org.covscript.lang.Cov_constantsKt.COV_NAME;
import static org.covscript.lang.Cov_constantsKt.COV_PKG_EXTENSION;

/**
 * @author ice1000
 */
public final class CovLanguage extends Language {
	public static final @NotNull CovLanguage INSTANCE = new CovLanguage();

	private CovLanguage() {
		super(COV_NAME, "text/" + COV_EXTENSION, "text/" + COV_PKG_EXTENSION);
	}

	@Override @Contract(pure = true) public boolean isCaseSensitive() {
		return false;
	}
}
