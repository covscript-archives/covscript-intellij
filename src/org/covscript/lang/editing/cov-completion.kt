package org.covscript.lang.editing

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.util.ProcessingContext
import org.covscript.lang.psi.CovTypes

class CovCompletionContributor : CompletionContributor() {
	private companion object Completions {
		private val fileHeaderCompletion = listOf(
				"import ",
				"package ",
				"using "
		).map(LookupElementBuilder::create)
		private val switchCompletion = listOf(
				"case ",
				"default",
				"end"
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

	init {
		extend(CompletionType.BASIC,
				psiElement(CovTypes.SYM)
						.afterLeaf("\n")
						.andNot(psiElement()
								.inside(psiElement(CovTypes.BODY_OF_SOMETHING))),
				CovProvider(fileHeaderCompletion))
		extend(CompletionType.BASIC,
				psiElement(PsiErrorElement::class.java)
						.afterLeaf("\n")
						.withAncestor(4, psiElement(CovTypes.SWITCH_STATEMENT))
						.andNot(psiElement().withAncestor(2, psiElement(CovTypes.BODY_OF_SOMETHING))),
				CovProvider(switchCompletion))
		extend(CompletionType.BASIC,
				psiElement(CovTypes.SYM)
						.afterLeaf("\n"),
				CovProvider(fileContentCompletion))
	}
}
