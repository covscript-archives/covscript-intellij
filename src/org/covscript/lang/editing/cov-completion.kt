package org.covscript.lang.editing

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import org.covscript.lang.psi.CovTypes

class CovBasicCompletionContributor : CompletionContributor() {
	companion object {
		private val KEYS = listOf("if", "namespace", "while", "var", "const var", "for")
	}

	init {
		extend(
				CompletionType.BASIC,
				PlatformPatterns.psiElement(CovTypes.STATEMENT),
				object : CompletionProvider<CompletionParameters>() {
					override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext?, resultSet: CompletionResultSet) {
						resultSet.addAllElements(KEYS.map(LookupElementBuilder::create))
					}
				})
	}
}