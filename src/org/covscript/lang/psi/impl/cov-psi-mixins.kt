package org.covscript.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.injected.StringLiteralEscaper
import com.intellij.psi.scope.PsiScopeProcessor
import org.covscript.lang.CovTokenType
import org.covscript.lang.psi.*

interface ICovVariableDeclaration : PsiNameIdentifierOwner {
	override fun getNameIdentifier(): CovSymbol
}

abstract class CovVariableDeclarationMixin(node: ASTNode) : CovVariableDeclaration, TrivialDeclaration(node) {
	private var idCache: CovSymbol? = null
	override fun getNameIdentifier() = idCache ?: (children.first { it is CovSymbol } as CovSymbol).also { idCache = it }
	override val startPoint: PsiElement get() = parent
	override fun subtreeChanged() {
		idCache = null
		super.subtreeChanged()
	}
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
	override fun getReferences(): Array<PsiReference> = refCache
			?: collectFrom(startPoint, nameIdentifier.text, nameIdentifier)
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
	val isVar: Boolean
	val isConstVar: Boolean
	val isFunctionName: Boolean
	val isStructName: Boolean
	val isNamespaceName: Boolean
	val isDeclaration: Boolean
}

abstract class CovSymbolMixin(node: ASTNode) : CovSymbol, ASTWrapperPsiElement(node) {
	private val refCache by lazy {
		object : CovSymbolRef() {
			override fun getElement() = this@CovSymbolMixin
		}
	}
	final override val isException: Boolean by lazy { parent is CovTryCatchStatement }
	final override val isLoopVar: Boolean by lazy { parent is CovForStatement }
	final override val isVar: Boolean by lazy { parent.let { it is CovVariableDeclaration && it.nameIdentifier === this } }
	final override val isConstVar: Boolean by lazy { isVar && prevSibling.prevSibling?.run { node.elementType == CovTypes.CONST_KEYWORD } == true }
	final override val isParameter: Boolean by lazy { parent.let { it is CovFunctionDeclaration && it.nameIdentifier !== this } }
	final override val isNamespaceName: Boolean by lazy { parent is CovNamespaceDeclaration }
	final override val isStructName: Boolean by lazy { parent is CovStructDeclaration }
	final override val isFunctionName: Boolean by lazy { parent.let { it is CovFunctionDeclaration && it.nameIdentifier === this } }
	final override val isDeclaration: Boolean by lazy {
		isException or
				isLoopVar or
				isVar or
				isConstVar or
				isNamespaceName or
				isFunctionName or
				isStructName or
				isParameter
	}

	override fun getReference() = refCache
	override fun getNameIdentifier() = this
	override fun setName(name: String) = CovTokenType.fromText(name, project).also { replace(it) }
}
