package org.covscript.lang.editing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import org.covscript.lang.CovBundle
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
			is CovCharLit -> {
				when (element.text.length) {
					2 -> holder.createErrorAnnotation(element, CovBundle.message("cov.lint.char-cannot-empty"))
					3 -> if (element.text[1] == '\\') holder.createErrorAnnotation(element,
							CovBundle.message("cov.lint.expect-escape"))
					4 -> if (element.text[1] == '\\') dealWithEscape(element, 2, element.text[2], holder)
					else holder.createErrorAnnotation(element, CovBundle.message("cov.lint.char-cannot-big"))
					else -> holder.createErrorAnnotation(element, CovBundle.message("cov.lint.char-cannot-big"))
				}
			}
			is CovBreak -> if (null == element.parentOfType(CovLoopUntilStatement::class, CovWhileStatement::class))
				holder.createErrorAnnotation(element, CovBundle.message("cov.lint.break-outside-loop"))
			is CovContinue -> if (null == element.parentOfType(CovLoopUntilStatement::class, CovWhileStatement::class))
				holder.createErrorAnnotation(element, CovBundle.message("cov.lint.continue-outside-loop"))
			is CovReturnStatement -> if (null == element.parentOfType(CovFunctionDeclaration::class))
				holder.createErrorAnnotation(element, CovBundle.message("cov.lint.return-outside-function"))
			is CovThrowStatement -> if (null == element.parentOfType(CovFunctionDeclaration::class, CovBodyOfSomething::class))
				holder.createErrorAnnotation(element, CovBundle.message("cov.lint.throw-outside-body"))
			is CovBlockStatement -> if (element.bodyOfSomething.statementList.size <= 1)
				holder.createWeakWarningAnnotation(element, CovBundle.message("cov.lint.unnecessary-block"))
						.registerFix(CovBlockToStatementIntention(element))
			is CovWhileStatement -> if (element.expr.text == "true")
				holder.createWeakWarningAnnotation(element, CovBundle.message("cov.lint.infinite-while"))
						.registerFix(CovReplaceWithTextIntention(element, "loop\n${element.bodyOfSomething.text}end",
								CovBundle.message("cov.lint.replace-with-loop")))
			is CovLoopUntilStatement -> element.expr?.run {
				if (text == "false") holder.createWeakWarningAnnotation(element,
						CovBundle.message("cov.lint.infinite-loop-until"))
						.registerFix(CovRemoveElementIntention(this, CovBundle.message("cov.lint.remove-until")))
			}
			is CovBracketExpr -> {
				val innerExpr = element.expr as? CovBracketExpr ?: return
				holder.createWeakWarningAnnotation(element, CovBundle.message("cov.lint.too-many-brackets"))
						.registerFix(CovReplaceWithElementIntention(element, innerExpr,
								CovBundle.message("cov.lint.remove-outer-brackets")))
			}
			is CovPlusOp -> {
				val left = element.children.first { it is CovExpr } as CovExpr
				val right = element.children.last { it is CovExpr } as CovExpr
				val infoText = CovBundle.message("cov.lint.constant-folding")
				when {
					left is CovString && right is CovString -> holder.createWeakWarningAnnotation(element, infoText)
							.registerFix(CovReplaceWithTextIntention(element,
									"${left.text.dropLast(1)}${right.text.drop(1)}",
									CovBundle.message("cov.lint.replace-with-concatenated")))
					left is CovNumber && right is CovNumber -> {
						val leftDec = BigDecimal(left.text)
						val rightDec = BigDecimal(right.text)
						holder.createWeakWarningAnnotation(element, infoText)
								.registerFix(CovReplaceWithTextIntention(element,
										(leftDec + rightDec).toPlainString(),
										CovBundle.message("cov.lint.replace-with-calculated")))
					}
				}
			}
			is CovNamespaceDeclaration -> holder.createInfoAnnotation(element.symbol, null)
					.textAttributes = CovSyntaxHighlighter.NAMESPACE_DEFINITION
			is CovFunctionDeclaration -> holder.createInfoAnnotation(element.children[1], null)
					.textAttributes = CovSyntaxHighlighter.FUNCTION_DEFINITION
			is CovVariableDeclaration -> holder.createInfoAnnotation(element.nameIdentifier, null)
					.textAttributes = CovSyntaxHighlighter.VARIABLE_DEFINITION
			is CovStructDeclaration -> holder.createInfoAnnotation(element.symbol, null)
					.textAttributes = CovSyntaxHighlighter.STRUCT_DEFINITION
			is CovCollapsedStatement ->
				if (element.children.any { it.javaClass.simpleName.startsWith("Cov") }) {
					holder.createInfoAnnotation(element, CovBundle.message("cov.lint.collapsed-block")).run {
						textAttributes = CovSyntaxHighlighter.BEGIN_END_BLOCK
						registerFix(collapsedToOneLine(element))
					}
					val firstLfIndex = element.text.indexOfFirst { it == '\n' }
					val firstIllegal = element.text.substring(BEGIN_BLOCK_LEN, firstLfIndex)
							.indexOfFirst { it !in EMPTY_CHARACTERS }
					if (firstIllegal >= 0) {
						val start = element.textRange.startOffset
						holder.createErrorAnnotation(TextRange(start + firstIllegal + BEGIN_BLOCK_LEN, start + firstLfIndex),
								CovBundle.message("cov.lint.collapsed-char-before-lf"))
					}
				} else holder.createWarningAnnotation(element,
						CovBundle.message("cov.lint.empty-collapsed-block"))
						.registerFix(CovRemoveElementIntention(element,
								CovBundle.message("cov.lint.remove-collapsed-block")))
		}
	}
}
