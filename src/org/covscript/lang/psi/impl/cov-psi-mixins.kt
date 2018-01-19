package org.covscript.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.covscript.lang.psi.CovVariableDeclaration

abstract class CovVariableDeclarationMixin(node: ASTNode) : CovVariableDeclaration, ASTWrapperPsiElement(node) {

}
