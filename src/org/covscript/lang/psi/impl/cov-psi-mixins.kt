package org.covscript.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import org.covscript.lang.CovLanguage
import org.covscript.lang.psi.*

abstract class CovVariableDeclarationMixin(node: ASTNode) :
		CovVariableDeclaration,
		ASTWrapperPsiElement(node),
		PsiNameIdentifierOwner {
	private var references: Array<PsiReference>? = null
	override fun getNameIdentifier() = symbol
	override fun setName(name: String) = PsiFileFactory
			.getInstance(project)
			.createFileFromText(CovLanguage, name)
			.firstChild

	override fun getReferences(): Array<PsiReference> = references ?: SyntaxTraverser
			.psiTraverser(parent.parent)
			.filter { it is CovSymbol && it.text == symbol.text }
			.mapNotNull { it.reference }
			.toList()
			.also(::println)
			.toTypedArray()
			.also { references = it }

	override fun subtreeChanged() {
		references = null
		super.subtreeChanged()
	}
}

abstract class CovSymbolMixin(node: ASTNode) :
		CovSymbol,
		ASTWrapperPsiElement(node) {
	private val refCache by lazy { CovSymbolRef(this) }
	override fun getReference() = if (parent is CovVariableDeclaration) null else refCache
}
