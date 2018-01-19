package org.covscript.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import org.covscript.lang.psi.*

abstract class CovVariableDeclarationMixin(node: ASTNode) : CovVariableDeclaration, ASTWrapperPsiElement(node) {
	override fun getReferences(): Array<PsiReference> = super.getReferences() // TODO
}

abstract class CovSymbolMixin(node: ASTNode) : CovSymbol, ASTWrapperPsiElement(node) {
	private val refCache by lazy { CovSymbolRef(this) }
	override fun getReference() = refCache
}
