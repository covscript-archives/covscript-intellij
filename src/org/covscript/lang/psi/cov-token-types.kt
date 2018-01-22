package org.covscript.lang.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.covscript.lang.CovLanguage

class CovTokenType(debugName: String) : IElementType(debugName, CovLanguage) {
	companion object {
		@JvmField val COMMENTS = TokenSet.create(CovTypes.COMMENT)
		@JvmField val STRINGS = TokenSet.create(CovTypes.STR, CovTypes.CHAR)
		@JvmField val CONCATENATABLE_TOKENS = TokenSet.orSet(COMMENTS, STRINGS)
		fun fromText(name: String, project: Project): PsiElement = PsiFileFactory
				.getInstance(project)
				.createFileFromText(CovLanguage, name)
				.firstChild
	}
}
