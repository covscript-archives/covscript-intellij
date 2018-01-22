package org.covscript.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.covscript.lang.psi.*

abstract class CovVariableDeclarationMixin(node: ASTNode) : CovVariableDeclaration, TrivialDeclaration(node) {
	override fun getNameIdentifier() = symbol
}

abstract class TrivialDeclaration(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner {
	private var references: Array<PsiReference>? = null
	override fun setName(name: String): PsiElement = CovTokenType.fromText(name, project).let(::replace)
	abstract override fun getNameIdentifier(): PsiElement
	override fun getReferences(): Array<PsiReference> = references ?: SyntaxTraverser
			.psiTraverser(parent.parent)
			.filter { it is CovSymbol && it.text == nameIdentifier.text }
			.mapNotNull(PsiElement::getReference)
			.toList()
			.toTypedArray()
			.also { references = it }

	override fun processDeclarations(
			processor: PsiScopeProcessor,
			substitutor: ResolveState,
			lastParent: PsiElement?,
			place: PsiElement) =
			PsiTreeUtil.isAncestor(this, place, true) ||
					processor.execute(nameIdentifier, substitutor)

	override fun subtreeChanged() {
		references = null
		super.subtreeChanged()
	}
}

abstract class CovFunctionDeclarationMixin(node: ASTNode) : CovFunctionDeclaration, TrivialDeclaration(node) {
	override fun getNameIdentifier() = symbol
}

abstract class CovSymbolMixin(node: ASTNode) :
		CovSymbol,
		ASTWrapperPsiElement(node) {
	private val refCache by lazy { CovSymbolRef(this) }
	override fun getReference() = if (parent is CovSuffixedExpression) refCache else null
}
