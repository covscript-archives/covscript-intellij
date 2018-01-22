package org.covscript.lang.psi

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil

class CovSymbolRef(symbol: CovSymbol, private var refTo: PsiElement? = null) :
		PsiPolyVariantReferenceBase<CovSymbol>(symbol, TextRange(0, symbol.textLength), true) {
	private val project = symbol.project
	override fun equals(other: Any?) = (other as? CovSymbolRef)?.element == element
	override fun hashCode() = element.hashCode()
	override fun getCanonicalText(): String = element.text
	override fun getVariants(): Array<out Any> {
		val variantsProcessor = SymbolResolveProcessor(this, true)
		treeWalkUp(element, variantsProcessor)
		return variantsProcessor.resultElement
	}
	override fun isReferenceTo(o: PsiElement?) = o == refTo ||
			(o as? PsiNameIdentifierOwner)?.nameIdentifier?.text == element.text

	override fun resolve() = refTo ?: super.resolve().also { refTo = it }
	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
		if (element.parent !is CovSuffixedExpression) return emptyArray()
		if (project.isDisposed) return emptyArray()
		return ResolveCache
				.getInstance(project)
				.resolveWithCaching(this, Resolver, true, incompleteCode)
	}

	private companion object Resolver : ResolveCache.PolyVariantResolver<CovSymbolRef> {
		override fun resolve(ref: CovSymbolRef, incompleteCode: Boolean): Array<out ResolveResult> {
			val currentSymbol = ref.element ?: return emptyArray()
			val processor = SymbolResolveProcessor(ref, incompleteCode)
			treeWalkUp(currentSymbol, processor)
			PsiTreeUtil
					.getParentOfType(currentSymbol, CovStatement::class.java)
					?.processDeclarations(processor, ResolveState.initial(), currentSymbol, processor.place)
			return processor.candidates
		}
	}
}

abstract class ResolveProcessor(val name: String) : PsiScopeProcessor {
	private var candidateSet = hashSetOf<PsiElementResolveResult>()
	val candidates get() = candidateSet.toTypedArray()
	val resultElement get() = candidates.map(LookupElementBuilder::create).toTypedArray()
	override fun handleEvent(event: PsiScopeProcessor.Event, o: Any?) = Unit
	fun addCandidate(candidate: PsiElementResolveResult) = candidateSet.add(candidate)
}

open class SymbolResolveProcessor(name: String, val place: PsiElement, val incompleteCode: Boolean) :
		ResolveProcessor(name) {
	constructor(ref: CovSymbolRef, incompleteCode: Boolean) : this(ref.canonicalText, ref.element, incompleteCode)

	private val processedElements = hashSetOf<PsiElement>()
	private fun addCandidate(symbol: CovSymbol) = addCandidate(PsiElementResolveResult(symbol, true))
	override fun <T : Any?> getHint(hintKey: Key<T>): T? = null
	override fun execute(element: PsiElement, resolveState: ResolveState) =
			if (element is CovSymbol && element !in processedElements) {
				val accessible = name == element.text
				if (accessible && !((element as? StubBasedPsiElement<*>)?.stub == null && PsiTreeUtil.hasErrorElements(element)))
					addCandidate(element)
				processedElements.add(element)
				!accessible
			} else true
}

fun treeWalkUp(place: PsiElement, processor: PsiScopeProcessor): Boolean {
	var lastParent: PsiElement? = null
	var run: PsiElement? = place
	while (run != null) {
		if (!run.processDeclarations(processor, ResolveState.initial(), lastParent, place)) return false
		lastParent = run
		run = run.parent
	}
	return true
}
