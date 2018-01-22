package org.covscript.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.covscript.lang.psi.*

abstract class CovVariableDeclarationMixin(node: ASTNode) : CovVariableDeclaration, TrivialDeclaration(node) {
	override fun getNameIdentifier() = symbol
	override val startPoint: PsiElement get() = parent.parent
}

abstract class TrivialDeclaration(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner {
	private var references: Array<PsiReference>? = null
	override fun setName(name: String): PsiElement = CovTokenType.fromText(name, project).let(::replace)
	abstract override fun getNameIdentifier(): PsiElement
	abstract val startPoint: PsiElement
	override fun getReferences(): Array<PsiReference> = references ?: collectFrom(startPoint, nameIdentifier.text)
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
	override val startPoint: PsiElement get() = parent.parent
}

abstract class CovParameterMixin(node: ASTNode) : CovParameter, TrivialDeclaration(node) {
	override fun getNameIdentifier() = this
	override val startPoint: PsiElement get() = parent
	override fun processDeclarations(
			processor: PsiScopeProcessor,
			substitutor: ResolveState,
			lastParent: PsiElement?,
			place: PsiElement) = processor.execute(nameIdentifier, substitutor)
}

abstract class CovSymbolMixin(node: ASTNode) :
		CovSymbol,
		ASTWrapperPsiElement(node) {
	private val refCache by lazy { CovSymbolRef(this) }
	private val isUsage get() = parent is CovSuffixedExpression
	override fun getReference() = if (isUsage) refCache else null
}
