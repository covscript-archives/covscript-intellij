package org.covscript.lang.psi

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import org.covscript.lang.CovLexerAdapter
import org.covscript.lang.CovTokenType

class CovFindUsageProvider : FindUsagesProvider {
	companion object Scanner : DefaultWordsScanner(CovLexerAdapter(),
			CovTokenType.SYMBOLS, CovTokenType.COMMENTS, CovTokenType.CONCATENATABLE_TOKENS)

	override fun canFindUsagesFor(element: PsiElement) = element is CovSymbol && element.isDeclaration
	override fun getWordsScanner() = Scanner
	override fun getHelpId(psiElement: PsiElement): String? = null
	override fun getDescriptiveName(element: PsiElement) = (element as? CovSymbol)?.text.orEmpty()
	override fun getType(element: PsiElement) = when (element) {
		is CovSymbol -> when {
			element.isFunctionName -> "Function"
			element.isConstVar -> "Const var"
			element.isException -> "Exception"
			else -> ""
		}
		is CovParameter -> "Parameter"
		else -> ""
	}

	override fun getNodeText(element: PsiElement, useFullName: Boolean) = buildString {
		append(getType(element))
		if (isNotEmpty()) append(" ")
		append((element as? PsiNameIdentifierOwner)?.name.orEmpty())
	}
}