@file:JvmName("CovPsiImplUtils")
@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER", "ConflictingExtensionProperty")

package org.covscript.lang.psi.impl

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.tree.IElementType
import org.covscript.lang.psi.*

fun CovExpression.primaryExprOrNull() =
		if (binaryOperator != null) null else leftPrimaryExprOrNull()

fun CovExpression.leftPrimaryExprOrNull() =
		suffixedExpression.takeIf {
			it.prefixOperator == null &&
					it.expressionList.isEmpty() &&
					it.suffixedExpressionList.isEmpty()
		}

fun CovForStatement.processDeclarations(
		processor: PsiScopeProcessor,
		substitutor: ResolveState,
		lastParent: PsiElement?,
		place: PsiElement) =
		if (!parameter.processDeclarations(processor, substitutor, lastParent, place)) false
		else processDeclTrivial(processor, substitutor, lastParent, place)

fun PsiElement.processDeclarations(
		processor: PsiScopeProcessor,
		substitutor: ResolveState,
		lastParent: PsiElement?,
		place: PsiElement) = processDeclTrivial(processor, substitutor, lastParent, place)

fun PsiElement.processDeclTrivial(
		processor: PsiScopeProcessor,
		substitutor: ResolveState,
		lastParent: PsiElement?,
		place: PsiElement): Boolean {
	var run: PsiElement? = lastParent?.prevSibling ?: lastChild
	while (run != null) {
		if (!run.processDeclarations(processor, substitutor, null, place)) return false
		run = run.prevSibling
	}
	return true
}

val CovStatement.allBlockStructure: PsiElement?
	get() = collapsedStatement ?: functionDeclaration ?: structDeclaration ?: namespaceDeclaration ?: forStatement
	?: loopUntilStatement ?: whileStatement ?: tryCatchStatement ?: switchStatement ?: blockStatement ?: ifStatement
	?: variableDeclaration

val CovCollapsedStatement.anythingInside: PsiElement?
	get() = functionDeclaration ?: structDeclaration ?: namespaceDeclaration ?: forStatement
	?: loopUntilStatement ?: whileStatement ?: tryCatchStatement ?: switchStatement ?: blockStatement ?: ifStatement
	?: variableDeclaration

fun collectFrom(startPoint: PsiElement, name: String) = SyntaxTraverser
		.psiTraverser(startPoint)
		.filter { it is CovSymbol && it.text == name }
		.mapNotNull(PsiElement::getReference)
		.toList()
		.toTypedArray()

val CovComment.tokenType: IElementType get() = CovTypes.COMMENT
val CovComment.isValidHost get() = true
fun CovComment.updateText(string: String): CovComment = replace(CovTokenType.fromText(string, project)) as CovComment
fun CovComment.createLiteralTextEscaper() = object : LiteralTextEscaper<CovComment>(this@createLiteralTextEscaper) {
	private var numOfSemicolon = 1
	override fun isOneLine() = true
	override fun getOffsetInHost(offsetInDecoded: Int, rangeInHost: TextRange) = offsetInDecoded + numOfSemicolon
	override fun decode(rangeInHost: TextRange, builder: StringBuilder): Boolean {
		numOfSemicolon = myHost.text.indexOfFirst { it != ';' }
		builder.append(myHost.text, rangeInHost.startOffset + numOfSemicolon, rangeInHost.endOffset)
		return true
	}
}
