package org.covscript.lang.editing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.covscript.lang.CovBundle
import org.covscript.lang.CovSyntaxHighlighter
import org.covscript.lang.psi.CovCollapsedStatement


const val EMPTY_CHARACTERS = " \t\r\n"
const val BEGIN_BLOCK_LEN = 6

fun dealWithEscape(element: PsiElement, index: Int, char: Char, holder: AnnotationHolder) {
	val range = TextRange(element.textRange.startOffset + index - 1, element.textRange.startOffset + index + 1)
	if (char !in "abfnrtv0\\\"'") holder.createErrorAnnotation(range, CovBundle.message("cov.lint.illegal-escape"))
	else holder.createInfoAnnotation(range, null).textAttributes = CovSyntaxHighlighter.STRING_ESCAPE
}

fun collapsedToOneLine(element: CovCollapsedStatement) =
		CovReplaceWithTextIntention(
				element,
				element.children.firstOrNull()?.text?.replace("\n", "") ?: element.text,
				CovBundle.message("cov.lint.convert-collapsed-block"))
