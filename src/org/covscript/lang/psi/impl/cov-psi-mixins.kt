package org.covscript.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.covscript.lang.CovLanguage
import org.covscript.lang.psi.*

abstract class CovVariableDeclarationMixin(node: ASTNode) :
		CovVariableDeclaration,
		ASTWrapperPsiElement(node),
		PsiNameIdentifierOwner {
	private var references: Array<PsiReference>? = null
	override fun getNameIdentifier() = symbol
	override fun setName(name: String): PsiElement = PsiFileFactory
			.getInstance(project)
			.createFileFromText(CovLanguage, name)
			.firstChild
			.let(::replace)

	override fun getReferences(): Array<PsiReference> = references ?: SyntaxTraverser
			.psiTraverser(parent.parent)
			.filter { it is CovSymbol && it.text == symbol.text }
			.mapNotNull(PsiElement::getReference)
			.toList()
			.toTypedArray()
			.also { references = it }

	override fun processDeclarations(
			processor: PsiScopeProcessor,
			substitutor: ResolveState,
			lastParent: PsiElement?,
			place: PsiElement) =
			PsiTreeUtil.isAncestor(this, place, true) or processor.execute(symbol, substitutor)

	override fun subtreeChanged() {
		references = null
		super.subtreeChanged()
	}
}

abstract class CovSymbolMixin(node: ASTNode) :
		CovSymbol,
		ASTWrapperPsiElement(node) {
	private val refCache by lazy { CovSymbolRef(this) }
	override fun getReference() = if (parent is CovSuffixedExpression) refCache else null
}
