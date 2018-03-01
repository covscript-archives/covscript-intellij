package org.covscript.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.injected.StringLiteralEscaper
import com.intellij.psi.scope.PsiScopeProcessor
import org.covscript.lang.CovTokenType
import org.covscript.lang.psi.*

abstract class CovVariableDeclarationMixin(node: ASTNode) : CovVariableDeclaration, TrivialDeclaration(node) {
	override fun getNameIdentifier() = children.first { it is CovSymbol }
	override val startPoint: PsiElement get() = parent
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
	override fun getNameIdentifier() = children.first { it is CovSymbol }
	override val startPoint: PsiElement get() = parent.parent
	override fun processDeclarations(
			processor: PsiScopeProcessor,
			substitutor: ResolveState,
			lastParent: PsiElement?,
			place: PsiElement): Boolean {
		symbolList.forEach {
			if (it != children[1] &&
					!it.processDeclarations(processor, substitutor, lastParent, place))
				return false
		}
		return super.processDeclarations(processor, substitutor, lastParent, place)
	}
}

abstract class CovCommentMixin(node: ASTNode) : ASTWrapperPsiElement(node), CovComment {
	override fun getTokenType() = node.elementType
	override fun isValidHost() = true
	override fun updateText(string: String): CovComment = replace(CovTokenType.fromText(string, project)) as CovComment
	override fun createLiteralTextEscaper() = StringLiteralEscaper.createSimple(this)
}

interface ICovStatement : PsiElement {
	val inside: PsiElement
}

abstract class CovStatementMixin(node: ASTNode) : ASTWrapperPsiElement(node), CovStatement {
	override val inside: PsiElement
		get() = if (children.size == 1) children.first() else children[1]

	override fun processDeclarations(
			processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
			processDeclTrivial(processor, substitutor, lastParent, place)
}

interface ICovExpr : PsiElement

abstract class CovExprMixin(node: ASTNode) : ASTWrapperPsiElement(node), CovExpr

abstract class CovBodyOfSomethingMixin(node: ASTNode) : ASTWrapperPsiElement(node), CovBodyOfSomething {
	override fun processDeclarations(
			processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
			processDeclTrivial(processor, substitutor, lastParent, place)
}

abstract class CovForStatementMixin(node: ASTNode) : ASTWrapperPsiElement(node), CovForStatement {
	override fun processDeclarations(
			processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
			if (!symbol.processDeclarations(processor, substitutor, lastParent, place)) false
			else processDeclTrivial(processor, substitutor, lastParent, place)
}

abstract class CovNamespaceDeclarationMixin(node: ASTNode) : CovNamespaceDeclaration, TrivialDeclaration(node) {
	override fun getNameIdentifier() = symbol
	override val startPoint: PsiElement get() = parent.parent
}

abstract class CovTryCatchDeclarationMixin(node: ASTNode) : CovTryCatchStatement, ASTWrapperPsiElement(node) {
	override fun processDeclarations(
			processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
			symbol.processDeclarations(processor, state, lastParent, place) and
					processDeclTrivial(processor, state, lastParent, place)
}

interface ICovSymbol : PsiNameIdentifierOwner {
	val isException: Boolean
	val isLoopVar: Boolean
	val isParameter: Boolean
	val isDeclaration: Boolean
}

abstract class CovSymbolMixin(node: ASTNode) : CovSymbol, ASTWrapperPsiElement(node) {
	private val refCache by lazy { CovSymbolRef(this) }
	final override val isException: Boolean by lazy { parent is CovTryCatchStatement }
	final override val isLoopVar: Boolean by lazy { parent is CovForStatement }
	final override val isParameter: Boolean by lazy { parent.let { it is CovFunctionDeclaration && it.children[1] != this } }
	final override val isDeclaration: Boolean by lazy {
		isException or
				isLoopVar or
				isParameter
	}

	override fun getReference() = refCache
	override fun getNameIdentifier() = if (isDeclaration) null else this
	override fun setName(name: String) = CovTokenType.fromText(name, project).let(::replace)
}
