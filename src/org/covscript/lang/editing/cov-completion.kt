package org.covscript.lang.editing

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.util.ProcessingContext
import org.covscript.lang.psi.CovTypes

class CovCompletionContributor : CompletionContributor() {
	private companion object Completions {
		private val basicCompletionList = listOf(
				"import ",
				"package ",
				"using ")
	}

	init {
		extend(CompletionType.BASIC, psiElement(CovTypes.SYM).afterLeaf("\n")
				, object :
				CompletionProvider<CompletionParameters>() {
			override fun addCompletions(
					parameters: CompletionParameters,
					context: ProcessingContext?,
					result: CompletionResultSet) {
				basicCompletionList.forEach { result.addElement(LookupElementBuilder.create(it)) }
			}
		})
	}
}
