package org.covscript.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import org.covscript.lang.psi.*

abstract class CovVariableDeclarationMixin(node: ASTNode) : CovVariableDeclaration, TrivialDeclaration(node) {
	override fun getNameIdentifier() = symbol
	override val startPoint: PsiElement get() = parent.parent.parent
}

abstract class TrivialDeclaration(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner {
	private var refCache: Array<PsiReference>? = null
	override fun setName(newName: String) = CovTokenType.fromText(newName, project).let(nameIdentifier::replace)
			.also {
				if (it is TrivialDeclaration)
					it.refCache = references.mapNotNull { it.handleElementRename(newName).reference }.toTypedArray()
			}

	override fun getName(): String = nameIdentifier.text
	abstract override fun getNameIdentifier(): PsiElement
	abstract val startPoint: PsiElement
	override fun getReferences(): Array<PsiReference> = refCache ?: collectFrom(startPoint, nameIdentifier.text)
			.also { refCache = it }

	override fun processDeclarations(
			processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
			processDeclTrivial(processor, substitutor, lastParent, place) and processor.execute(nameIdentifier, substitutor)

	override fun subtreeChanged() {
		refCache = null
		super.subtreeChanged()
	}
}

abstract class CovFunctionDeclarationMixin(node: ASTNode) : CovFunctionDeclaration, TrivialDeclaration(node) {
	override fun getNameIdentifier() = symbol
	override val startPoint: PsiElement get() = parent.parent
	override fun processDeclarations(
			processor: PsiScopeProcessor,
			substitutor: ResolveState,
			lastParent: PsiElement?,
			place: PsiElement): Boolean {
		parameterList.forEach { if (!it.processDeclarations(processor, substitutor, lastParent, place)) return false }
		return super.processDeclarations(processor, substitutor, lastParent, place)
	}
}

abstract class CovNamespaceDeclarationMixin(node: ASTNode) : CovNamespaceDeclaration, TrivialDeclaration(node) {
	override fun getNameIdentifier() = symbol
	override val startPoint: PsiElement get() = parent.parent
}

abstract class CovTryCatchDeclarationMixin(node: ASTNode) : CovTryCatchStatement, ASTWrapperPsiElement(node) {
	override fun processDeclarations(
			processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
			parameter.processDeclarations(processor, state, lastParent, place) &&
					super.processDeclarations(processor, state, lastParent, place)
}

abstract class CovParameterMixin(node: ASTNode) : CovParameter, TrivialDeclaration(node) {
	override fun getNameIdentifier() = this
	override val startPoint: PsiElement get() = parent
}

abstract class CovSymbolMixin(node: ASTNode) : CovSymbol, ASTWrapperPsiElement(node) {
	private val refCache by lazy { CovSymbolRef(this) }
	private val isUsage get() = parent is CovSuffixedExpression
	override fun getReference() = if (isUsage) refCache else null
}
