package org.covscript.lang.editing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.covscript.lang.CovBundle
import org.covscript.lang.CovSyntaxHighlighter
import org.covscript.lang.psi.*
import java.math.BigDecimal

class CovAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is CovString -> string(element, holder)
			is CovCharLit -> charLit(element, holder)
			is CovBreak -> jump(element, holder)
			is CovContinue -> jump(element, holder)
			is CovReturnStatement -> returnStatement(element, holder)
			is CovThrowStatement -> throwStatement(element, holder)
			is CovBlockStatement -> blockStatement(element, holder)
			is CovWhileStatement -> whileStatement(element, holder)
			is CovLoopUntilStatement -> loopUntilStatement(element, holder)
			is CovBracketExpr -> bracketedExpr(element, holder)
			is CovPlusOp -> plusOp(element, holder)
			is CovMinusOp -> minusOp(element, holder)
			is CovNamespaceDeclaration -> holder.createInfoAnnotation(element.symbol, null)
					.textAttributes = CovSyntaxHighlighter.NAMESPACE_DEFINITION
			is CovFunctionDeclaration -> functionDeclaration(element, holder)
			is CovVariableDeclaration -> holder.createInfoAnnotation(element.nameIdentifier, null)
					.textAttributes = CovSyntaxHighlighter.VARIABLE_DEFINITION
			is CovStructDeclaration -> holder.createInfoAnnotation(element.nameIdentifier, null)
					.textAttributes = CovSyntaxHighlighter.STRUCT_DEFINITION
			is CovCollapsedStatement -> collapsedStatement(element, holder)
		}
	}

	private fun functionDeclaration(
			element: CovFunctionDeclaration,
			holder: AnnotationHolder) {
		holder
				.createInfoAnnotation(element.nameIdentifier, null)
				.textAttributes = CovSyntaxHighlighter.FUNCTION_DEFINITION
	}

	private fun plusOp(element: CovPlusOp, holder: AnnotationHolder) {
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

	private fun minusOp(element: CovMinusOp, holder: AnnotationHolder) {
		val left = element.children.first { it is CovExpr } as CovExpr
		val right = element.children.last { it is CovExpr } as CovExpr
		val infoText = CovBundle.message("cov.lint.constant-folding")
		when {
			left is CovNumber && right is CovNumber -> {
				val leftDec = BigDecimal(left.text)
				val rightDec = BigDecimal(right.text)
				holder.createWeakWarningAnnotation(element, infoText)
						.registerFix(CovReplaceWithTextIntention(element,
								(leftDec - rightDec).toPlainString(),
								CovBundle.message("cov.lint.replace-with-calculated")))
			}
		}
	}

	private fun collapsedStatement(
			element: CovCollapsedStatement,
			holder: AnnotationHolder) {
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

	private fun throwStatement(
			element: PsiElement,
			holder: AnnotationHolder) {
		if (null == PsiTreeUtil.getParentOfType(element,
						CovFunctionDeclaration::class.java,
						CovBodyOfSomething::class.java))
			holder.createErrorAnnotation(element, CovBundle.message("cov.lint.throw-outside-body"))
	}

	private fun blockStatement(
			element: CovBlockStatement,
			holder: AnnotationHolder) {
		if (element.bodyOfSomething.statementList.size <= 1)
			holder.createWeakWarningAnnotation(element, CovBundle.message("cov.lint.unnecessary-block"))
					.registerFix(CovBlockToStatementIntention(element))
	}

	private fun whileStatement(
			element: CovWhileStatement,
			holder: AnnotationHolder) {
		if (element.expr.text == "true")
			holder.createWeakWarningAnnotation(element, CovBundle.message("cov.lint.infinite-while"))
					.registerFix(CovReplaceWithTextIntention(element, "loop\n${element.bodyOfSomething.text}end",
							CovBundle.message("cov.lint.replace-with-loop")))
	}

	private fun loopUntilStatement(
			element: CovLoopUntilStatement,
			holder: AnnotationHolder) {
		element.expr?.run {
			if (text == "false") holder.createWeakWarningAnnotation(element,
					CovBundle.message("cov.lint.infinite-loop-until"))
					.registerFix(CovRemoveElementIntention(this, CovBundle.message("cov.lint.remove-until")))
		}
	}

	private fun bracketedExpr(
			element: CovBracketExpr,
			holder: AnnotationHolder) {
		val innerExpr = element.expr as? CovBracketExpr ?: return
		holder.createWeakWarningAnnotation(element, CovBundle.message("cov.lint.too-many-brackets"))
				.registerFix(CovReplaceWithElementIntention(element, innerExpr,
						CovBundle.message("cov.lint.remove-outer-brackets")))
	}

	private fun returnStatement(element: PsiElement, holder: AnnotationHolder) {
		if (null == PsiTreeUtil.getParentOfType(element, CovFunctionDeclaration::class.java))
			holder.createErrorAnnotation(element, CovBundle.message("cov.lint.return-outside-function"))
	}

	private fun jump(element: PsiElement, holder: AnnotationHolder) {
		if (null == PsiTreeUtil.getParentOfType(element,
						CovLoopUntilStatement::class.java,
						CovWhileStatement::class.java))
			holder.createErrorAnnotation(element, CovBundle.message("cov.lint.statement-outside-loop", element.text))
	}

	private fun string(element: CovString, holder: AnnotationHolder) {
		var isPrefixedByBackslash = false
		element.text.forEachIndexed { index, char ->
			isPrefixedByBackslash = if (isPrefixedByBackslash) {
				dealWithEscape(element, index, char, holder)
				false
			} else char == '\\'
		}
	}

	private fun charLit(element: CovCharLit, holder: AnnotationHolder) {
		when (element.text.length) {
			2 -> holder.createErrorAnnotation(element, CovBundle.message("cov.lint.char-cannot-empty"))
			3 -> if (element.text[1] == '\\') holder.createErrorAnnotation(element,
					CovBundle.message("cov.lint.expect-escape"))
			4 -> if (element.text[1] == '\\') dealWithEscape(element, 2, element.text[2], holder)
			else holder.createErrorAnnotation(element, CovBundle.message("cov.lint.char-cannot-big"))
			else -> holder.createErrorAnnotation(element, CovBundle.message("cov.lint.char-cannot-big"))
					.registerFix(CovReplaceWithTextIntention(element,
							"\"${element.text.substring(1, element.textLength - 2)}\"",
							CovBundle.message("cov.lint.char-to-string")))
		}
	}
}
