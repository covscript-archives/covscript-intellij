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
import icons.CovIcons
import org.covscript.lang.CovTokenType

class CovSymbolRef(private val symbol: PsiElement, private var refTo: PsiElement? = null) :
		PsiPolyVariantReference {
	override fun getElement() = symbol
	override fun getRangeInElement() = TextRange(0, element.textLength)
	override fun bindToElement(element: PsiElement) = element.also { refTo = it }
	override fun isSoft() = true
	override fun equals(other: Any?) = (other as? CovSymbolRef)?.element == element
	override fun hashCode() = element.hashCode()
	override fun getCanonicalText(): String = element.text
	override fun handleElementRename(newName: String) = CovTokenType.fromText(newName, element.project).let(element::replace)
	override fun getVariants(): Array<out Any> {
		val variantsProcessor = CompletionProcessor(this, true)
		treeWalkUp(variantsProcessor, element, element.containingFile, ResolveState.initial())
		return variantsProcessor.candidateSet.toTypedArray()
	}

	override fun isReferenceTo(o: PsiElement?) = o == refTo ||
			(o as? PsiNameIdentifierOwner)?.nameIdentifier?.text == element.text

	override fun resolve() = refTo ?: multiResolve(false).firstOrNull()?.element.also { refTo = it }
	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
		if (element.parent !is CovSuffixedExpression) return emptyArray()
		if (element.project.isDisposed) return emptyArray()
		return ResolveCache
				.getInstance(element.project)
				.resolveWithCaching(this, Resolver, true, incompleteCode)
	}

	private companion object Resolver : ResolveCache.PolyVariantResolver<CovSymbolRef> {
		override fun resolve(ref: CovSymbolRef, incompleteCode: Boolean): Array<out ResolveResult> {
			val processor = SymbolResolveProcessor(ref, incompleteCode)
			treeWalkUp(processor, ref.element, ref.element.containingFile, ResolveState.initial())
			PsiTreeUtil
					.getParentOfType(ref.element, CovStatement::class.java)
					?.processDeclarations(processor, ResolveState.initial(), ref.element, processor.place)
			return processor.candidateSet.toTypedArray()
		}
	}
}

abstract class ResolveProcessor<ResolveResult>(val place: PsiElement) : PsiScopeProcessor {
	abstract val candidateSet: ArrayList<ResolveResult>
	override fun handleEvent(event: PsiScopeProcessor.Event, o: Any?) = Unit
	override fun <T : Any?> getHint(hintKey: Key<T>): T? = null
	protected val PsiElement.hasNoError get() = (this as? StubBasedPsiElement<*>)?.stub != null || !PsiTreeUtil.hasErrorElements(this)

	protected fun isInScope(element: PsiElement) = PsiTreeUtil.isAncestor(
			if (element is CovParameter) element.parent
			else element.parent?.parent?.parent, place, false)
}

class SymbolResolveProcessor(private val name: String, place: PsiElement, val incompleteCode: Boolean) :
		ResolveProcessor<PsiElementResolveResult>(place) {
	constructor(ref: CovSymbolRef, incompleteCode: Boolean) : this(ref.canonicalText, ref.element, incompleteCode)

	override val candidateSet = ArrayList<PsiElementResolveResult>(3)
	fun accessible(element: PsiElement) = name == element.text && isInScope(element)
	override fun execute(element: PsiElement, resolveState: ResolveState) = when {
		candidateSet.isNotEmpty() -> false
		element is CovSymbol -> {
			val accessible = accessible(element)
			if (accessible) candidateSet += PsiElementResolveResult(element, element.hasNoError)
			!accessible
		}
		else -> true
	}
}

class CompletionProcessor(place: PsiElement, val incompleteCode: Boolean) :
		ResolveProcessor<LookupElementBuilder>(place) {
	constructor(ref: CovSymbolRef, incompleteCode: Boolean) : this(ref.element, incompleteCode)

	override val candidateSet = ArrayList<LookupElementBuilder>(20)
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean {
		if (element.hasNoError and isInScope(element)) {
			val type = when (element) {
				is CovParameter -> "Parameter"
				is CovSymbol -> "Variable"
				else -> return true
			}
			candidateSet += LookupElementBuilder.create(element.text)
					.withIcon(CovIcons.COV_BIG_ICON)
					.withTypeText(type)
		}
		return true
	}
}

class CovRefactoringSupportProvider : RefactoringSupportProvider() {
	override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?) = true
}
