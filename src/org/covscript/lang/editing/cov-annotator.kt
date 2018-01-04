package org.covscript.lang.editing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.covscript.lang.CovSyntaxHighlighter
import org.covscript.lang.psi.*

class CovAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is CovString -> {
				var isPrefixedByBackslash = false
				element.text.forEachIndexed { index, char ->
					if (isPrefixedByBackslash) dealWithEscape(element, index, char, holder)
					isPrefixedByBackslash = char == '\\'
				}
			}
			is CovCharLiteral -> {
				when (element.text.length) {
					2 -> holder.createErrorAnnotation(element, "Char literal cannot be empty")
					3 -> if (element.text[1] == '\\') holder.createErrorAnnotation(element, "Escape character expected")
					4 -> if (element.text[1] == '\\') dealWithEscape(element, 2, element.text[2], holder)
					else holder.createErrorAnnotation(element, "Char literal cannot be more than 1 character")
					else -> holder.createErrorAnnotation(element, "Char literal cannot be more than 1 character")
				}
			}
			is CovCollapsedStatement -> holder.createInfoAnnotation(element, "Collapsed into one line")
					.textAttributes = CovSyntaxHighlighter.BEGIN_END_BLOCK
		}
	}

	private fun dealWithEscape(element: PsiElement, index: Int, char: Char, holder: AnnotationHolder) {
		val range = TextRange(element.textRange.startOffset + index - 1, element.textRange.startOffset + index + 1)
		if (char !in "abfnrtv0\\\"'") holder.createErrorAnnotation(range, "Illegal escape character")
		else holder.createInfoAnnotation(range, null).textAttributes = CovSyntaxHighlighter.STRING_ESCAPE
	}
}