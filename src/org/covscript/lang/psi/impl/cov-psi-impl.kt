@file:JvmName("CovPsiImplUtils")
@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER", "ConflictingExtensionProperty")

package org.covscript.lang.psi.impl

import com.intellij.psi.PsiElement
import org.covscript.lang.psi.*

fun CovExpression.primaryExprOrNull() =
		if (binaryOperator != null) null else leftPrimaryExprOrNull()

fun CovExpression.leftPrimaryExprOrNull() =
		suffixedExpression.takeIf {
			it.prefixOperator == null &&
					it.expressionList.isEmpty() &&
					it.suffixedExpressionList.isEmpty()
		}

val CovStatement.allBlockStructure: PsiElement?
	get() = collapsedStatement ?: functionDeclaration ?: structDeclaration ?: namespaceDeclaration ?: forStatement
	?: loopUntilStatement ?: whileStatement ?: tryCatchStatement ?: switchStatement ?: blockStatement ?: ifStatement
	?: variableDeclaration

val CovCollapsedStatement.anythingInside: PsiElement?
	get() = functionDeclaration ?: structDeclaration ?: namespaceDeclaration ?: forStatement
	?: loopUntilStatement ?: whileStatement ?: tryCatchStatement ?: switchStatement ?: blockStatement ?: ifStatement
	?: variableDeclaration
