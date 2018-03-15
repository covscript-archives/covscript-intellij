package org.covscript.lang.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import org.covscript.lang.CovTokenType

class CovStringManipulator : AbstractElementManipulator<CovString>() {
	override fun handleContentChange(psi: CovString, range: TextRange, new: String): CovString {
		val after = CovTokenType.fromText(new, psi.project) as? CovString ?: return psi
		psi.replace(after)
		return after
	}
}
