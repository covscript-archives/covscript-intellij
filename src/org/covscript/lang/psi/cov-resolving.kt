package org.covscript.lang.psi

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.covscript.lang.psi.impl.treeWalkUp

class CovSymbolRef(symbol: CovSymbol, private var refTo: PsiElement? = null) :
		PsiPolyVariantReferenceBase<CovSymbol>(symbol, TextRange(0, symbol.textLength), true) {
	private val project = symbol.project
	override fun equals(other: Any?) = (other as? CovSymbolRef)?.element == element
	override fun hashCode() = element.hashCode()
	override fun getCanonicalText(): String = element.text
	override fun handleElementRename(newName: String) = CovTokenType.fromText(newName, project).let(element::replace)
	override fun getVariants(): Array<out Any> {
		val variantsProcessor = CompletionProcessor(this, true)
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

abstract class ResolveProcessor : PsiScopeProcessor {
	private var candidateSet = hashSetOf<PsiElementResolveResult>()
	val candidates get() = candidateSet.toTypedArray()
	val resultElement get() = candidates.map(LookupElementBuilder::create).toTypedArray()
	override fun handleEvent(event: PsiScopeProcessor.Event, o: Any?) = Unit
	fun addCandidate(symbol: PsiElement) = addCandidate(PsiElementResolveResult(symbol, true))
	fun addCandidate(candidate: PsiElementResolveResult) = candidateSet.add(candidate)
	fun hasCandidate(candidate: PsiElement) = candidateSet.any { it.element == candidate }
}

class SymbolResolveProcessor(private val name: String, val place: PsiElement, val incompleteCode: Boolean) :
		ResolveProcessor() {
	constructor(ref: CovSymbolRef, incompleteCode: Boolean) : this(ref.canonicalText, ref.element, incompleteCode)

	private val processedElements = hashSetOf<PsiElement>()
	override fun <T : Any?> getHint(hintKey: Key<T>): T? = null
	override fun execute(element: PsiElement, resolveState: ResolveState) =
			if ((element is CovSymbol || element is CovParameter) && element !in processedElements) {
				val accessible = name == element.text
				if (accessible && !((element as? StubBasedPsiElement<*>)?.stub == null && PsiTreeUtil.hasErrorElements(element)))
					addCandidate(element)
				processedElements.add(element)
				!accessible
			} else true
}

class CompletionProcessor(val place: PsiElement, val incompleteCode: Boolean) :
		ResolveProcessor() {
	constructor(ref: CovSymbolRef, incompleteCode: Boolean) : this(ref.element, incompleteCode)

	override fun <T : Any?> getHint(hintKey: Key<T>): T? = null
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean {
		if ((element is CovSymbol || element is CovParameter) && hasCandidate(element)) {
			if (!((element as? StubBasedPsiElement<*>)?.stub == null && PsiTreeUtil.hasErrorElements(element)))
				addCandidate(element)
		}
		return true
	}
}

class CovRefactoringSupportProvider : RefactoringSupportProvider() {
	override fun isInplaceRenameAvailable(element: PsiElement, context: PsiElement?) = element is CovSymbol
	override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?) = element is CovSymbol
}
