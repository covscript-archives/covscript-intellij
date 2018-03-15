package org.covscript.lang.editing

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.covscript.lang.psi.*
import org.intellij.lang.regexp.RegExpLanguage

/**
 * @author ice1000
 */
class CovLanguageInjector : LanguageInjector {
	override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
		if (host !is CovString) return
		if (host.parent is CovApplyFunction) {
			val aniki = host.parent.children.firstOrNull() as? CovMemberAccess ?: return
			val regex = aniki.firstChild ?: return
			val dot = regex.nextSibling ?: return
			val build = dot.nextSibling ?: return
			if (regex.text != "regex" || dot.node.elementType != CovTypes.DOT || build.text != "build")
				return
			places.addPlace(RegExpLanguage.INSTANCE, TextRange(1, host.textLength - 1), null, null)
		}
	}
}
