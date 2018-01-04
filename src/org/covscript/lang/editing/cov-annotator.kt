package org.covscript.lang.editing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.covscript.lang.CovSyntaxHighlighter
import org.covscript.lang.psi.CovString

class CovAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		if (element is CovString) {
			var isPrefixedByBackslash = false
			element.text.forEachIndexed { index, char ->
				if (isPrefixedByBackslash) {
					val range = TextRange(element.textRange.startOffset + index - 1, element.textRange.startOffset + index + 1)
					if (char !in "abfnrtv0\\\"'") holder.createErrorAnnotation(range, "Illegal escape character")
					else holder.createInfoAnnotation(range, null).textAttributes = CovSyntaxHighlighter.STRING_ESCAPE
				}
				isPrefixedByBackslash = char == '\\'
			}
		}
	}
}