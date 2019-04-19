package org.covscript.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.util.IncorrectOperationException
import org.covscript.lang.CovTokenType
import org.covscript.lang.orTrue
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

abstract class CovImportDeclarationMixin(node: ASTNode) : CovImportDeclaration, TrivialDeclaration(node) {
	override fun getNameIdentifier() = symbolList.first()
	// workaround for KT-23219
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String): TrivialDeclaration =
			throw IncorrectOperationException("Cannot rename import statement")
}

abstract class CovUsingDeclarationMixin(node: ASTNode) : CovUsingDeclaration, TrivialDeclaration(node) {
	override fun getNameIdentifier() = symbolList.lastOrNull()
	// workaround for KT-23219
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String): TrivialDeclaration =
			throw IncorrectOperationException("Cannot rename import statement")
}

abstract class CovVariableDeclarationMixin(node: ASTNode) : CovVariableDeclaration, ASTWrapperPsiElement(node) {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
			variableInitializationList.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class CovParametersMixin(node: ASTNode) : CovParameters, ASTWrapperPsiElement(node) {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
			parameterList.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class CovVariableInitializationMixin(node: ASTNode) : CovVariableInitialization, TrivialDeclaration(node) {
	override fun getNameIdentifier() = firstChild
}

abstract class CovParameterMixin(node: ASTNode) : CovParameter, TrivialDeclaration(node) {
	override fun getNameIdentifier() = firstChild
}

abstract class TrivialDeclaration(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner {
	override fun setName(newName: String) = also {
		nameIdentifier.run { CovTokenType.fromText(newName, project).let(::replace) }
	}

	override fun getName() = nameIdentifier?.text
	override fun processDeclarations(
			processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
			nameIdentifier?.processDeclarations(processor, substitutor, lastParent, place).orTrue()
}

abstract class CovFunctionDeclarationMixin(node: ASTNode) : CovFunctionDeclaration, TrivialDeclaration(node) {
	private var nameCache: PsiElement? = null
	override fun getNameIdentifier() = nameCache
			?: children.firstOrNull { it is CovSymbol }.also { nameCache = it }

	override fun processDeclarations(
			processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
			parameters?.processDeclarations(processor, substitutor, lastParent, place).orTrue() &&
					symbol?.processDeclarations(processor, substitutor, lastParent, place).orTrue()

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
		get() = children.firstOrNull()?.takeUnless { it is CovExpr }

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

abstract class CovGeneralBodyMixin(node: ASTNode) : ASTWrapperPsiElement(node), CovGeneralBody {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
			bodyOfSomething?.processDeclarations(processor, state, lastParent, place).orTrue()
}

abstract class CovForStatementMixin(node: ASTNode) : TrivialDeclaration(node), CovForStatement {
	override fun getNameIdentifier() = symbol
}

abstract class CovForEachStatementMixin(node: ASTNode) : TrivialDeclaration(node), CovForEachStatement {
	override fun getNameIdentifier() = children.firstOrNull { it is CovSymbol }
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
	final override val isVar: Boolean get() = parent.let { it is CovVariableInitialization && it.nameIdentifier === this }
	final override val isConstVar: Boolean get() = isVar && parent?.parent?.firstChild?.firstChild?.node?.elementType in CovTokenType.CONST_DECLARER
	final override val isParameter: Boolean get() = parent.let { it is CovFunctionDeclaration && it.nameIdentifier !== this }
	final override val isNamespaceName: Boolean get() = parent is CovNamespaceDeclaration
	final override val isStructName: Boolean get() = parent.let { it is CovStructDeclaration && it.nameIdentifier === this }
	final override val isFunctionName: Boolean get() = parent.let { it is CovFunctionDeclaration && it.nameIdentifier === this }
	final override val isImportedName: Boolean get() = parent.let { it is CovImportDeclaration && it.nameIdentifier === this }
	final override val isUsingedName: Boolean get() = parent.let { it is CovUsingDeclaration && it.nameIdentifier === this }
	final override val isDeclaration: Boolean
		get() = isException ||
				isLoopVar ||
				isVar ||
				isConstVar ||
				isNamespaceName ||
				isUsingedName ||
				isImportedName ||
				isFunctionName ||
				isStructName ||
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
