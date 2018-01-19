package org.covscript.lang.psi

import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache

class CovSymbolRef(symbol: CovSymbol) : PsiPolyVariantReferenceBase<CovSymbol>(symbol, true) {
	private val project = symbol.project
	override fun equals(other: Any?) = (other as? CovSymbolRef)?.element == element
	override fun hashCode() = element.hashCode()
	override fun getVariants(): Array<Any> = arrayOf()
	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> = ResolveCache
			.getInstance(project)
			.resolveWithCaching(this, Resolver, true, incompleteCode)

	private companion object Resolver : ResolveCache.PolyVariantResolver<CovSymbolRef> {
		override fun resolve(ref: CovSymbolRef, incompleteCode: Boolean): Array<ResolveResult> {
			val currentSymbol = ref.element ?: return emptyArray()
			if (currentSymbol.parent !is CovSuffixedExpression) return emptyArray()
			var currentLookingScope = ref.element.parent
			val ret = arrayListOf<PsiElementResolveResult>()
			while (currentLookingScope != null) {
				val candidate = currentLookingScope
						.children
						.mapNotNull { (it as? CovVariableDeclaration)?.symbol }
						.firstOrNull { it.text == currentSymbol.text }
				candidate?.let(::PsiElementResolveResult)?.let(ret::add)
				currentLookingScope = currentLookingScope.parent
			}
			return ret.toTypedArray()
		}
	}
}
