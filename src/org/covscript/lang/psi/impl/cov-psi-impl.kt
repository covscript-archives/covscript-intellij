@file:JvmName("CovPsiImplUtils")
@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER", "ConflictingExtensionProperty")

package org.covscript.lang.psi.impl

import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import org.covscript.lang.psi.CovCollapsedStatement
import org.covscript.lang.psi.CovSymbol

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

val CovCollapsedStatement.anythingInside: PsiElement? get() = children.getOrNull(1)

fun collectFrom(startPoint: PsiElement, name: String, self: PsiElement? = null) = SyntaxTraverser
		.psiTraverser(startPoint)
		.filter { it is CovSymbol && it !== self && it.text == name }
		.mapNotNull(PsiElement::getReference)
		.toList()
		.toTypedArray()

fun treeWalkUp(
		processor: PsiScopeProcessor,
		entrance: PsiElement,
		maxScope: PsiElement?,
		state: ResolveState = ResolveState.initial()): Boolean {
	if (!entrance.isValid) return false
	var prevParent = entrance
	var scope: PsiElement? = entrance

	while (scope != null) {
		ProgressIndicatorProvider.checkCanceled()
		if (!scope.processDeclarations(processor, state, prevParent, entrance)) return false
		if (scope == maxScope) break
		prevParent = scope
		scope = prevParent.context
	}
	return true
}

