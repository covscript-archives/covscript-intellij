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

class CovSymbolRef constructor(private val symbol: CovSymbol) : PsiPolyVariantReference {
	private var refTo: PsiNameIdentifierOwner? = null
	override fun getElement() = symbol
	override fun getRangeInElement() = TextRange(0, element.textLength)
	override fun bindToElement(ref: PsiElement) = ref.also { refTo = it as? PsiNameIdentifierOwner }
	override fun isSoft() = true
	override fun equals(other: Any?) = (other as? CovSymbolRef)?.element == element
	override fun hashCode() = element.hashCode()
	override fun getCanonicalText(): String = element.text
	override fun handleElementRename(newName: String) = CovTokenType
			.fromText(newName, element.project)
			.let(element::replace)
			.also { refTo?.setName(newName) }

	override fun getVariants(): Array<LookupElementBuilder> {
		val variantsProcessor = CompletionProcessor(this, true)
		val file = element.containingFile ?: return emptyArray()
		treeWalkUp(variantsProcessor, element, file)
		return variantsProcessor.candidateSet.toTypedArray()
	}

	override fun isReferenceTo(o: PsiElement?) = o === refTo || o === resolve()
	override fun resolve() = refTo
			?: multiResolve(false)
					.firstOrNull()?.element.also { refTo = it as? PsiNameIdentifierOwner }

	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
		if (element.isDeclaration or !element.isValid or element.project.isDisposed) return emptyArray()
		val file = element.containingFile ?: return emptyArray()
		return ResolveCache
				.getInstance(element.project)
				.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<CovSymbolRef> { ref, incompleteCode ->
			val processor = SymbolResolveProcessor(ref, incompleteCode)
			val file = ref.element.containingFile ?: return@PolyVariantResolver emptyArray()
			treeWalkUp(processor, ref.element, file)
			processor.candidateSet.toTypedArray()
		}
	}
}

abstract class ResolveProcessor<ResolveResult>(private val place: PsiElement) : PsiScopeProcessor {
	abstract val candidateSet: ArrayList<ResolveResult>
	override fun handleEvent(event: PsiScopeProcessor.Event, o: Any?) = Unit
	override fun <AnyNullable> getHint(hintKey: Key<AnyNullable>): AnyNullable? where AnyNullable : Any? = null
	protected val PsiElement.hasNoError get() = (this as? StubBasedPsiElement<*>)?.stub != null || !PsiTreeUtil.hasErrorElements(this)

	protected fun isInScope(element: PsiElement) = if (element is CovSymbol) when {
		element.isParameter -> PsiTreeUtil.isAncestor(element.parent, place, true)
		element.isDeclaration -> PsiTreeUtil.isAncestor(
				PsiTreeUtil.getParentOfType(element, CovStatement::class.java)?.parent, place, false)
		else -> false
	} else false
}

class SymbolResolveProcessor(private val name: String, place: PsiElement, val incompleteCode: Boolean) :
		ResolveProcessor<PsiElementResolveResult>(place) {
	constructor(ref: CovSymbolRef, incompleteCode: Boolean) : this(ref.canonicalText, ref.element, incompleteCode)

	override val candidateSet = ArrayList<PsiElementResolveResult>(3)
	fun accessible(element: PsiElement) = element.hasNoError && name == element.text && isInScope(element)
	override fun execute(element: PsiElement, resolveState: ResolveState) = when {
		candidateSet.isNotEmpty() -> false
		element is CovSymbol -> {
			val accessible = accessible(element) and element.isDeclaration
			if (accessible) candidateSet += PsiElementResolveResult(element, element.hasNoError)
			!accessible
		}
		else -> true
	}
}

class CompletionProcessor(place: PsiElement, val incompleteCode: Boolean) :
		ResolveProcessor<LookupElementBuilder>(place) {
	constructor(ref: CovSymbolRef, incompleteCode: Boolean) : this(ref.element, incompleteCode)

	override val candidateSet = ArrayList<LookupElementBuilder>(30)
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean {
		if (!(element.hasNoError and isInScope(element))) return true
		if (element !is CovSymbol || !element.isDeclaration) return true
		val (type, icon) = when {
			element.isParameter -> "Parameter" to CovIcons.VARIABLE_ICON
			element.isException -> "Exception" to CovIcons.TRY_CATCH_ICON
			element.isVar -> "Variable" to CovIcons.VARIABLE_ICON
			element.isConstVar -> "Constant" to CovIcons.VARIABLE_ICON
			element.isFunctionName -> "Function" to CovIcons.FUNCTION_ICON
			element.isImportedName or
					element.isUsingedName or
					element.isNamespaceName -> "Namespace" to CovIcons.NAMESPACE_ICON
			element.isLoopVar -> "Loop var" to CovIcons.VARIABLE_ICON
			element.isStructName -> "Struct" to CovIcons.STRUCT_ICON
			else -> "<Unknown>" to CovIcons.COV_BIG_ICON
		}
		candidateSet += LookupElementBuilder.create(element.text)
				.withIcon(icon)
				.withTypeText(type)
		return true
	}
}

class CovRefactoringSupportProvider : RefactoringSupportProvider() {
	override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?) = true
}
