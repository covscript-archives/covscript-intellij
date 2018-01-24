package org.covscript.lang.psi

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiTreeUtil.treeWalkUp

class CovSymbolRef(private val symbol: CovSymbol, private var refTo: PsiElement? = null) :
		PsiPolyVariantReference {
	private val project = symbol.project
	override fun getElement() = symbol
	override fun getRangeInElement() = TextRange(0, symbol.textLength)
	override fun bindToElement(element: PsiElement) = element.also { refTo = it }
	override fun isSoft() = true
	override fun equals(other: Any?) = (other as? CovSymbolRef)?.element == element
	override fun hashCode() = symbol.hashCode()
	override fun getCanonicalText(): String = symbol.text
	override fun handleElementRename(newName: String) = CovTokenType.fromText(newName, project).let(symbol::replace)
	override fun getVariants(): Array<out Any> {
		val variantsProcessor = CompletionProcessor(this, true)
		treeWalkUp(variantsProcessor, symbol, symbol.containingFile, ResolveState.initial())
		return variantsProcessor.resultElement.toTypedArray()
	}

	override fun isReferenceTo(o: PsiElement?) = o == refTo ||
			(o as? PsiNameIdentifierOwner)?.nameIdentifier?.text == symbol.text

	override fun resolve() = refTo ?: multiResolve(false).firstOrNull()?.element.also { refTo = it }
	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
		if (element.parent !is CovSuffixedExpression) return emptyArray()
		if (project.isDisposed) return emptyArray()
		return ResolveCache
				.getInstance(project)
				.resolveWithCaching(this, Resolver, true, incompleteCode)
	}

	private companion object Resolver : ResolveCache.PolyVariantResolver<CovSymbolRef> {
		override fun resolve(ref: CovSymbolRef, incompleteCode: Boolean): Array<out ResolveResult> {
			val processor = SymbolResolveProcessor(ref, incompleteCode)
			treeWalkUp(processor, ref.symbol, ref.symbol.containingFile, ResolveState.initial())
			PsiTreeUtil
					.getParentOfType(ref.symbol, CovStatement::class.java)
					?.processDeclarations(processor, ResolveState.initial(), ref.symbol, processor.place)
			return processor.candidateSet.toTypedArray()
		}
	}
}

abstract class ResolveProcessor<ResolveResult>(val place: PsiElement) : PsiScopeProcessor {
	val candidateSet = hashSetOf<ResolveResult>()
	override fun handleEvent(event: PsiScopeProcessor.Event, o: Any?) = Unit
	protected val PsiElement.canResolve get() = this is CovSymbol || this is CovParameter
	protected val PsiElement.hasNoError get() = (this as? StubBasedPsiElement<*>)?.stub != null || !PsiTreeUtil.hasErrorElements(this)
	fun addCandidate(candidate: ResolveResult) = candidateSet.add(candidate)

	protected fun isInScope(element: PsiElement) = PsiTreeUtil.isAncestor(
			if (element is CovParameter) element.parent
			else element.parent?.parent?.parent, place, false)
}

class SymbolResolveProcessor(private val name: String, place: PsiElement, val incompleteCode: Boolean) :
		ResolveProcessor<PsiElementResolveResult>(place) {
	constructor(ref: CovSymbolRef, incompleteCode: Boolean) : this(ref.canonicalText, ref.element, incompleteCode)

	private fun addCandidate(symbol: PsiElement) = addCandidate(PsiElementResolveResult(symbol, true))
	private val processedElements = hashSetOf<PsiElement>()
	override fun <T : Any?> getHint(hintKey: Key<T>): T? = null
	override fun execute(element: PsiElement, resolveState: ResolveState) = when {
		candidateSet.isNotEmpty() -> false
		element.canResolve && element !in processedElements -> {
			val accessible = name == element.text && isInScope(element)
			if (accessible and element.hasNoError) addCandidate(element)
			processedElements.add(element)
			!accessible
		}
		else -> true
	}
}

class CompletionProcessor(place: PsiElement, val incompleteCode: Boolean) : ResolveProcessor<String>(place) {
	constructor(ref: CovSymbolRef, incompleteCode: Boolean) : this(ref.element, incompleteCode)

	val resultElement get() = candidateSet.map(LookupElementBuilder::create)
	override fun <T : Any?> getHint(hintKey: Key<T>): T? = null
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean {
		if (element.canResolve and element.hasNoError and isInScope(element)) {
			val symbol = element.text
			if (symbol !in candidateSet) addCandidate(symbol)
		}
		return true
	}
}

class CovRefactoringSupportProvider : RefactoringSupportProvider() {
	override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?) = true
}
