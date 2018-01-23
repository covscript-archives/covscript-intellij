//package org.covscript.lang.psi
//
//import com.intellij.lang.cacheBuilder.DefaultWordsScanner
//import com.intellij.lang.findUsages.FindUsagesProvider
//import com.intellij.psi.*
//import org.covscript.lang.CovLexerAdapter
//
//class CovFindUsageProvider : FindUsagesProvider {
//	companion object Scanner : DefaultWordsScanner(CovLexerAdapter(),
//			CovTokenType.SYMBOLS, CovTokenType.COMMENTS, CovTokenType.CONCATENATABLE_TOKENS)
//
//	override fun canFindUsagesFor(element: PsiElement) = element is CovSymbol
//	override fun getWordsScanner() = Scanner
//	override fun getHelpId(psiElement: PsiElement): String? = null
//	override fun getDescriptiveName(element: PsiElement) = (element as? CovSymbol)?.text.orEmpty()
//	override fun getType(element: PsiElement) = when (element) {
//		is CovFunctionDeclaration -> "function"
//		is CovParameter -> "parameter"
//		is CovVariableDeclaration -> "variable"
//		is CovForStatement -> "for looper"
//		else -> "symbol"
//	}
//
//	override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
//		val builder = StringBuilder(getType(element))
//		if (builder.isNotEmpty()) builder.append(" ")
//		builder.append((element as? PsiNameIdentifierOwner)?.name.orEmpty())
//		return builder.toString()
//	}
//}