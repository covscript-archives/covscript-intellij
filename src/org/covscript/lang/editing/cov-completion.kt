package org.covscript.lang.editing

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.covscript.lang.psi.*

class CovCompletionContributor : CompletionContributor() {
	private companion object Completions {
		private val fileHeaderCompletion = listOf(
				"import ",
				"package ",
				"using "
		).map(LookupElementBuilder::create)
		private val loopCompletion = listOf(
				"break",
				"continue"
		).map(LookupElementBuilder::create)
		private val functionCompletion = listOf(
				"return "
		).map(LookupElementBuilder::create)
		private val fileContentCompletion = listOf(
				"if ",
				"for ",
				"loop ",
				"while ",
				"block",
				"function ",
				"namespace ",
				"struct ",
				"@begin ",
				"switch ",
				"var ",
				"throw ",
				"try ",
				"end"
		).map(LookupElementBuilder::create)
	}

	private class CovProvider(private val list: List<LookupElement>) :
			CompletionProvider<CompletionParameters>() {
		override fun addCompletions(
				parameters: CompletionParameters,
				context: ProcessingContext?,
				result: CompletionResultSet) = list.forEach(result::addElement)
	}

	override fun invokeAutoPopup(position: PsiElement, typeChar: Char) =
			position !is CovComment && typeChar in " \t\n.("

	init {
		extend(CompletionType.BASIC,
				psiElement(CovTypes.SYM)
						.afterLeaf("\n")
						.andNot(
								psiElement()
										.inside(psiElement(CovTypes.BODY_OF_SOMETHING))),
				CovProvider(fileHeaderCompletion))
		extend(CompletionType.BASIC,
				psiElement(CovTypes.SYM)
						.afterLeaf("\n")
						.inside(
								psiElement(CovTypes.BODY_OF_SOMETHING))
						.andOr(
								psiElement()
										.inside(CovWhileStatement::class.java),
								psiElement()
										.inside(CovLoopUntilStatement::class.java)),
				CovProvider(loopCompletion))
		extend(CompletionType.BASIC,
				psiElement(CovTypes.SYM)
						.afterLeaf("\n")
						.inside(
								psiElement(CovTypes.BODY_OF_SOMETHING))
						.andOr(
								psiElement()
										.inside(CovFunctionDeclaration::class.java)),
				CovProvider(functionCompletion))
		extend(CompletionType.BASIC,
				psiElement(CovTypes.SYM)
						.afterLeaf("\n"),
				CovProvider(fileContentCompletion))
	}
}
