@file:JvmName("CovPsiImplUtils")
@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER", "ConflictingExtensionProperty")

package org.covscript.lang.psi.impl

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

val CovCollapsedStatement.anythingInside: PsiElement? get() = children[1]

fun collectFrom(startPoint: PsiElement, name: String) = SyntaxTraverser
		.psiTraverser(startPoint)
		.filter { it is CovSymbol && it.text == name }
		.mapNotNull(PsiElement::getReference)
		.toList()
		.toTypedArray()
