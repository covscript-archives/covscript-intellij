package org.covscript.lang.editing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.covscript.lang.CovSyntaxHighlighter
import org.covscript.lang.psi.*
import java.math.BigDecimal

class CovAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is CovString -> {
				var isPrefixedByBackslash = false
				element.text.forEachIndexed { index, char ->
					isPrefixedByBackslash = if (isPrefixedByBackslash) {
						dealWithEscape(element, index, char, holder)
						false
					} else char == '\\'
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
			is CovBlockStatement -> {
				val list = element.bodyOfSomething.statementList
				if (list.size <= 1) holder.createWeakWarningAnnotation(element, "Unnecessary block declaration")
						.registerFix(CovBlockToStatementIntention(element))
			}
			is CovExpression -> {
				if (element.parent is CovExpression) return
				val left = element.leftPrimaryExprOrNull() ?: return
				val right = element.expression?.primaryExprOrNull() ?: return
				val op = element.binaryOperator ?: return
				val infoText = "Constant folding is possible"
				when {
					left.string != null && right.string != null -> {
						if (op.text == "+") holder.createInfoAnnotation(element, infoText)
								.registerFix(CovReplaceWithTextIntention(element,
										"${left.text.dropLast(1)}${right.text.drop(1)}",
										"Replace with concatenated string"))
						else if (op.text != ":") holder.createErrorAnnotation(element, "Operator ${op.text} is not applicable between strings")
					}
					left.number != null && right.number != null -> {
						val leftDec = BigDecimal(left.text)
						val rightDec = BigDecimal(right.text)
						val fixText = "Replace with calculated result"
						when (op.text) {
							"+" -> holder.createInfoAnnotation(element, infoText).registerFix(CovReplaceWithTextIntention(element,
									(leftDec + rightDec).toPlainString(), fixText))
							"-" -> holder.createInfoAnnotation(element, infoText).registerFix(CovReplaceWithTextIntention(element,
									(leftDec - rightDec).toPlainString(), fixText))
							"*" -> holder.createInfoAnnotation(element, infoText).registerFix(CovReplaceWithTextIntention(element,
									(leftDec * rightDec).toPlainString(), fixText))
							"/" -> holder.createInfoAnnotation(element, infoText).registerFix(CovReplaceWithTextIntention(element,
									(leftDec / rightDec).toPlainString(), fixText))
						}
					}
				}
			}
			is CovNamespaceDeclaration -> holder.createInfoAnnotation(element.symbol, null)
					.textAttributes = CovSyntaxHighlighter.NAMESPACE_DEFINITION
			is CovFunctionDeclaration -> holder.createInfoAnnotation(element.symbol, null)
					.textAttributes = CovSyntaxHighlighter.FUNCTION_DEFINITION
			is CovVariableDeclaration -> holder.createInfoAnnotation(element.symbol, null)
					.textAttributes = CovSyntaxHighlighter.VARIABLE_DEFINITION
			is CovStructDeclaration -> holder.createInfoAnnotation(element.symbol, null)
					.textAttributes = CovSyntaxHighlighter.STRUCT_DEFINITION
			is CovCollapsedStatement ->
				if (element.primaryStatement != null) holder.createInfoAnnotation(element, "Collapsed into one line").run {
					textAttributes = CovSyntaxHighlighter.BEGIN_END_BLOCK
					registerFix(CovCollapsedBlockToOneStatementIntention(element))
				}
				else holder.createWarningAnnotation(element, "Empty collapsed block")
						.registerFix(CovRemoveBlockIntention(element, "Remove empty collapsed block"))
		}
	}

	private fun dealWithEscape(element: PsiElement, index: Int, char: Char, holder: AnnotationHolder) {
		val range = TextRange(element.textRange.startOffset + index - 1, element.textRange.startOffset + index + 1)
		if (char !in "abfnrtv0\\\"'") holder.createErrorAnnotation(range, "Illegal escape character")
		else holder.createInfoAnnotation(range, null).textAttributes = CovSyntaxHighlighter.STRING_ESCAPE
	}
}