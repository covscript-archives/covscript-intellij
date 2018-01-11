package org.covscript.lang.psi

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.covscript.lang.CovLanguage

class CovTokenType(debugName: String) : IElementType(debugName, CovLanguage) {
	companion object {
		@JvmField val COMMENTS = TokenSet.create(CovTypes.COMMENT)
		@JvmField val STRINGS = TokenSet.create(CovTypes.STR)
	}
}