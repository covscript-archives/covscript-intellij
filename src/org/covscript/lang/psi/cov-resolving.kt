package org.covscript.lang.psi

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import icons.CovIcons
import org.covscript.lang.CovTokenType
import org.covscript.lang.psi.impl.treeWalkUp

abstract class CovSymbolRef(private var refTo: PsiElement? = null) :
		PsiPolyVariantReference {
	abstract override fun getElement(): CovSymbol
	override fun getRangeInElement() = TextRange(0, element.textLength)
	override fun bindToElement(ref: PsiElement) = element.also { refTo = ref }
	override fun isSoft() = true
	override fun equals(other: Any?) = (other as? CovSymbolRef)?.element == element
	override fun hashCode() = element.hashCode()
	override fun getCanonicalText(): String = element.text
	override fun handleElementRename(newName: String) = CovTokenType.fromText(newName, element.project).also { element.replace(it) }
	override fun getVariants(): Array<out Any> {
		val variantsProcessor = CompletionProcessor(this, true)
		val file = element.containingFile ?: return emptyArray()
		treeWalkUp(variantsProcessor, element, file)
		return variantsProcessor.candidateSet.toTypedArray()
	}

	override fun isReferenceTo(o: PsiElement?) = o == refTo ||
			(o as? PsiNameIdentifierOwner)?.nameIdentifier?.text == element.text

	override fun resolve() = refTo ?: multiResolve(false).firstOrNull()?.element.also { refTo = it }
	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
		if (element.isDeclaration) return emptyArray()
		if (element.project.isDisposed) return emptyArray()
		return ResolveCache
				.getInstance(element.project)
				.resolveWithCaching(this, Resolver, true, incompleteCode)
	}

	private companion object Resolver : ResolveCache.PolyVariantResolver<CovSymbolRef> {
		override fun resolve(ref: CovSymbolRef, incompleteCode: Boolean): Array<out ResolveResult> {
			val processor = SymbolResolveProcessor(ref, incompleteCode)
			val file = ref.element.containingFile ?: return emptyArray()
			treeWalkUp(processor, ref.element, file)
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
	override fun <AnyNullable> getHint(hintKey: Key<AnyNullable>): AnyNullable? where AnyNullable : Any? = null
	protected val PsiElement.hasNoError get() = (this as? StubBasedPsiElement<*>)?.stub != null || !PsiTreeUtil.hasErrorElements(this)

	protected fun isInScope(element: PsiElement) = PsiTreeUtil.isAncestor(
			if (element is CovSymbol && element.isParameter) element.parent
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
			if (element !is CovSymbol) return true
			val (type, icon) = when {
				element.isParameter -> "Parameter" to CovIcons.VARIABLE_ICON
				element.isException -> "Exception" to CovIcons.TRY_CATCH_ICON
				element.isLocalVar -> "Local variable" to CovIcons.VARIABLE_ICON
				element.isFunctionName -> "Function" to CovIcons.FUNCTION_ICON
				element.isNamespaceName -> "Namespace" to CovIcons.NAMESPACE_ICON
				element.isLoopVar -> "Loop variable" to CovIcons.VARIABLE_ICON
				else -> "<Unknown>" to CovIcons.COV_BIG_ICON
			}
			candidateSet += LookupElementBuilder.create(element.text)
					.withIcon(icon)
					.withTypeText(type)
		}
		return true
	}
}

class CovRefactoringSupportProvider : RefactoringSupportProvider() {
	override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?) = true
}
