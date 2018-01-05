package org.covscript.lang.editing

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import org.covscript.lang.psi.CovTypes

class CovBasicCompletionContributor : CompletionContributor() {
	companion object {
		private val COMPLETION_CANDIDATES = listOf("import ", "using ", "package ")
	}

	init {
		extend(
				CompletionType.BASIC,
				PlatformPatterns.psiElement(CovTypes.FILE_HEADER),
				object : CompletionProvider<CompletionParameters>() {
					override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext?, resultSet: CompletionResultSet) {
						resultSet.addAllElements(COMPLETION_CANDIDATES.map(LookupElementBuilder::create))
					}
				})
	}
}