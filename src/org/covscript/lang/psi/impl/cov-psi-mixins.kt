package org.covscript.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import org.covscript.lang.CovTokenType
import org.covscript.lang.orFalse
import org.covscript.lang.psi.*

interface ICovString : PsiLanguageInjectionHost {
	override fun createLiteralTextEscaper(): LiteralTextEscaper<out CovString>
	override fun updateText(s: String): CovString
}

@Suppress("HasPlatformType")
abstract class CovStringMixin(node: ASTNode) : ASTWrapperPsiElement(node), CovString {
	override fun isValidHost() = true
	override fun createLiteralTextEscaper() = LiteralTextEscaper.createSimple(this)
	override fun updateText(s: String) = ElementManipulators.handleContentChange(this, s)
}

interface ICovImportDeclaration : PsiNameIdentifierOwner

abstract class CovImportDeclarationMixin(node: ASTNode) : CovImportDeclaration, TrivialDeclaration(node) {
	override fun getNameIdentifier() = symbol
	// workaround for KT-23219
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String): TrivialDeclaration =
			throw IncorrectOperationException("Cannot rename import statement")
}

interface ICovUsingDeclaration : PsiNameIdentifierOwner

abstract class CovUsingDeclarationMixin(node: ASTNode) : CovUsingDeclaration, TrivialDeclaration(node) {
	override fun getNameIdentifier() = symbolList.lastOrNull()
	// workaround for KT-23219
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String): TrivialDeclaration =
			throw IncorrectOperationException("Cannot rename import statement")
}

interface ICovVariableDeclaration : PsiNameIdentifierOwner

abstract class CovVariableDeclarationMixin(node: ASTNode) : CovVariableDeclaration, TrivialDeclaration(node) {
	private var idCache: PsiElement? = null
	override fun getNameIdentifier() = idCache
			?: children.firstOrNull { it is CovSymbol }.also { idCache = it }

	override fun subtreeChanged() {
		idCache = null
		super.subtreeChanged()
	}
}

abstract class TrivialDeclaration(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner {
	private var refCache: Array<PsiReference>? = null
	override fun setName(newName: String) = also {
		nameIdentifier.run { CovTokenType.fromText(newName, project).let(::replace) }
		references.forEach { it.handleElementRename(newName) }
	}

	override fun getName() = nameIdentifier?.text
	open val startPoint: PsiElement
		get() = PsiTreeUtil.getParentOfType(this, CovStatement::class.java, true)?.parent ?: parent

	override fun getReferences() = refCache
			?: nameIdentifier
					?.let { collectFrom(startPoint, it.text, it) }
					?.also { refCache = it }
			?: emptyArray()

	override fun processDeclarations(
			processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
			nameIdentifier?.processDeclarations(processor, substitutor, lastParent, place).orFalse() and
					processDeclTrivial(processor, substitutor, lastParent, place)

	override fun subtreeChanged() {
		refCache = null
		super.subtreeChanged()
	}
}

interface ICovFunctionDeclaration : PsiNameIdentifierOwner

abstract class CovFunctionDeclarationMixin(node: ASTNode) : CovFunctionDeclaration, TrivialDeclaration(node) {
	private var nameCache: PsiElement? = null
	override fun getNameIdentifier() = nameCache
			?: children.firstOrNull { it is CovSymbol }.also { nameCache = it }

	override fun processDeclarations(
			processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
			symbolList.asReversed().all {
				it.processDeclarations(processor, substitutor, lastParent, place)
			} and processDeclTrivial(processor, substitutor, lastParent, place)

	override fun subtreeChanged() {
		nameCache = null
		super.subtreeChanged()
	}
}

interface ICovStatement : PsiElement {
	val inside: PsiElement?
}

abstract class CovStatementMixin(node: ASTNode) : ASTWrapperPsiElement(node), CovStatement {
	override val inside: PsiElement?
		get() = children.firstOrNull()?.takeIf {
			it is CovFunctionDeclaration ||
					it is CovNamespaceDeclaration ||
					it is CovVariableDeclaration ||
					it is CovForStatement ||
					it is CovStructDeclaration ||
					it is CovLoopUntilStatement ||
					it is CovIfStatement ||
					it is CovWhileStatement ||
					it is CovSwitchStatement ||
					it is CovBlockStatement ||
					it is CovTryCatchStatement
		}

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

abstract class CovForStatementMixin(node: ASTNode) : TrivialDeclaration(node), CovForStatement {
	override fun getNameIdentifier() = symbol
}

abstract class CovNamespaceDeclarationMixin(node: ASTNode) : CovNamespaceDeclaration, TrivialDeclaration(node) {
	override fun getNameIdentifier() = symbol
}

interface ICovStructDeclaration : PsiNameIdentifierOwner {
	override fun getNameIdentifier(): CovExpr?
}

abstract class CovStructDeclarationMixin(node: ASTNode) : CovStructDeclaration, TrivialDeclaration(node) {
	override fun getNameIdentifier() = exprList.firstOrNull()
}

abstract class CovTryCatchDeclarationMixin(node: ASTNode) : CovTryCatchStatement, TrivialDeclaration(node) {
	override fun getNameIdentifier() = symbol
}

interface ICovSymbol : PsiNameIdentifierOwner, CovExpr {
	val isException: Boolean
	val isLoopVar: Boolean
	val isParameter: Boolean
	val isVar: Boolean
	val isConstVar: Boolean
	val isFunctionName: Boolean
	val isStructName: Boolean
	val isNamespaceName: Boolean
	val isDeclaration: Boolean
	val isImportedName: Boolean
	val isUsingedName: Boolean
}

abstract class CovSymbolMixin(node: ASTNode) : CovSymbol, CovExprMixin(node) {
	private var referenceImpl: CovSymbolRef? = null
	final override val isException: Boolean get() = parent.let { it is CovTryCatchStatement && it.nameIdentifier === this }
	final override val isLoopVar: Boolean get() = parent.let { it is CovForStatement && it.nameIdentifier === this }
	final override val isVar: Boolean get() = parent.let { it is CovVariableDeclaration && it.nameIdentifier === this }
	final override val isConstVar: Boolean get() = isVar && parent.firstChild.node.elementType == CovTypes.CONST_KEYWORD
	final override val isParameter: Boolean get() = parent.let { it is CovFunctionDeclaration && it.nameIdentifier !== this }
	final override val isNamespaceName: Boolean get() = parent is CovNamespaceDeclaration
	final override val isStructName: Boolean get() = parent.let { it is CovStructDeclaration && it.nameIdentifier === this }
	final override val isFunctionName: Boolean get() = parent.let { it is CovFunctionDeclaration && it.nameIdentifier === this }
	final override val isImportedName: Boolean get() = parent.let { it is CovImportDeclaration && it.nameIdentifier === this }
	final override val isUsingedName: Boolean get() = parent.let { it is CovUsingDeclaration && it.nameIdentifier === this }
	final override val isDeclaration: Boolean
		get() = isException or
				isLoopVar or
				isVar or
				isConstVar or
				isNamespaceName or
				isUsingedName or
				isImportedName or
				isFunctionName or
				isStructName or
				isParameter

	override fun processDeclarations(
			processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
			processor.execute(this, state)

	override fun getReference() = referenceImpl ?: CovSymbolRef(this).also { referenceImpl = it }
	override fun getNameIdentifier() = this
	override fun getName() = text
	override fun setName(name: String) = CovTokenType.fromText(name, project).also { replace(it) }
	override fun subtreeChanged() {
		referenceImpl = null
		super.subtreeChanged()
	}
}
