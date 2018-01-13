package org.covscript.lang.editing

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import com.intellij.lang.*
import com.intellij.lang.refactoring.NamesValidator
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.highlighter.HighlighterIterator
import com.intellij.openapi.project.Project
import com.intellij.patterns.*
import com.intellij.pom.PomTargetPsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.refactoring.rename.RenameInputValidator
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.spellchecker.tokenizer.Tokenizer
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider
import com.intellij.util.ProcessingContext
import org.covscript.lang.*
import org.covscript.lang.psi.*

class CovBraceMatcher : PairedBraceMatcher {
	companion object {
		private val PAIRS = arrayOf(
				BracePair(CovTypes.LEFT_BRACKET, CovTypes.RIGHT_BRACKET, false),
				BracePair(CovTypes.LEFT_B_BRACKET, CovTypes.RIGHT_B_BRACKET, false),
				BracePair(CovTypes.LEFT_S_BRACKET, CovTypes.RIGHT_S_BRACKET, false),
				BracePair(CovTypes.FUNCTION_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.NAMESPACE_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.WHILE_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.FOR_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.IF_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.STRUCT_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.TRY_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.LOOP_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.BLOCK_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.SWITCH_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.CASE_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.DEFAULT_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.COLLAPSER_BEGIN, CovTypes.COLLAPSER_END, false)
		)
	}

	override fun getCodeConstructStart(psiFile: PsiFile?, openingBraceOffset: Int) = openingBraceOffset
	override fun isPairedBracesAllowedBeforeType(type: IElementType, iElementType: IElementType?) = true
	override fun getPairs() = PAIRS
}

class CovCommenter : Commenter {
	override fun getCommentedBlockCommentPrefix() = blockCommentPrefix
	override fun getCommentedBlockCommentSuffix() = blockCommentSuffix
	override fun getBlockCommentPrefix() = null
	override fun getBlockCommentSuffix() = null
	override fun getLineCommentPrefix() = "# "
}

class CovQuoteHandler : SimpleTokenSetQuoteHandler(CovTokenType.STRINGS) {
	override fun hasNonClosedLiteral(editor: Editor?, iterator: HighlighterIterator?, offset: Int) = true
}

class CovSpellCheckingStrategy : SpellcheckingStrategy() {
	override fun getTokenizer(element: PsiElement): Tokenizer<*> = when (element) {
		is CovComment, is CovSymbol -> super.getTokenizer(element)
		is CovString -> super.getTokenizer(element).takeIf { it != EMPTY_TOKENIZER } ?: TEXT_TOKENIZER
		else -> EMPTY_TOKENIZER
	}
}

class CovNamesValidator : NamesValidator, RenameInputValidator {
	override fun isKeyword(s: String, project: Project?) = s in COV_KEYWORDS
	override fun isInputValid(s: String, o: PsiElement, c: ProcessingContext) = isIdentifier(s, o.project)
	override fun getPattern(): ElementPattern<out PsiElement> = PlatformPatterns.psiElement().with(object :
			PatternCondition<PsiElement>("") {
		override fun accepts(element: PsiElement, context: ProcessingContext?) =
				(element as? PomTargetPsiElement)?.navigationElement is CovSymbol
	})

	override fun isIdentifier(name: String, project: Project?) = with(CovLexerAdapter()) {
		start(name)
		tokenType == CovTypes.SYM && tokenEnd == name.length
	}
}

const val SHORT_TEXT_MAX = 8
const val LONG_TEXT_MAX = 16
private fun cutText(it: String, textMax: Int) = if (it.length <= textMax) it else "${it.take(textMax)}…"

class CovBreadCrumbProvider : BreadcrumbsProvider {
	override fun getLanguages() = arrayOf(CovLanguage)
	override fun acceptElement(o: PsiElement) = o is CovFunctionDeclaration ||
			o is CovStructDeclaration ||
			o is CovNamespaceDeclaration

	override fun getElementTooltip(o: PsiElement) = when (o) {
		is CovFunctionDeclaration -> "function: <${o.text}>"
		is CovStructDeclaration -> "struct: <${o.text}>"
		is CovNamespaceDeclaration -> "namespace: <${o.text}>"
		else -> "??"
	}

	override fun getElementInfo(o: PsiElement): String = cutText(when (o) {
		is CovFunctionDeclaration -> o.symbol.text
		is CovStructDeclaration -> o.symbol.text
		is CovNamespaceDeclaration -> o.symbol.text
		else -> "??"
	}, SHORT_TEXT_MAX)
}
