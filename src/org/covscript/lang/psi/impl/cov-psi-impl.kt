@file:JvmName("CovPsiImplUtils")
@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package org.covscript.lang.psi.impl

import org.covscript.lang.psi.CovExpression

fun CovExpression.primaryExprOrNull() =
		if (binaryOperator != null) null else leftPrimaryExprOrNull()

fun CovExpression.leftPrimaryExprOrNull() =
		if (prefixOperator != null || suffixedExpression.expressionList.isNotEmpty()) null
		else suffixedExpression.primaryExpression

