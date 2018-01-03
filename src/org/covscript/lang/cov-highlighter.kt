package org.covscript.lang

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import org.covscript.lang.psi.CovTypes

class CovSyntaxHighlighter : SyntaxHighlighter {
	companion object {
		@JvmField val KEYWORDS = TextAttributesKey.createTextAttributesKey("COV_KEYWORDS", DefaultLanguageHighlighterColors.KEYWORD)
		@JvmField val COMMENTS = TextAttributesKey.createTextAttributesKey("COV_COMMENTS", DefaultLanguageHighlighterColors.LINE_COMMENT)
		private val KEYWORDS_KEY = arrayOf(KEYWORDS)
		private val COMMENTS_KEY = arrayOf(COMMENTS)
		private val KEYWORDS_LIST = listOf(
				CovTypes.IF_KEYWORD,
				CovTypes.ELSE_KEYWORD,
				CovTypes.END_KEYWORD,
				CovTypes.NEW_KEYWORD,
				CovTypes.GCNEW_KEYWORD,
				CovTypes.WHILE_KEYWORD,
				CovTypes.PACKAGE_KEYWORD,
				CovTypes.USING_KEYWORD,
				CovTypes.TRUE_KEYWORD,
				CovTypes.FALSE_KEYWORD,
				CovTypes.NULL_KEYWORD,
				CovTypes.IMPORT_KEYWORD,
				CovTypes.VAR_KEYWORD,
				CovTypes.CONST_KEYWORD,
				CovTypes.NAMESPACE_KEYWORD,
				CovTypes.FUNCTION_KEYWORD,
				CovTypes.BREAK_KEYWORD,
				CovTypes.CONTINUE_KEYWORD
		)
	}

	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		in KEYWORDS_LIST -> KEYWORDS_KEY
		CovTypes.LINE_COMMENT -> COMMENTS_KEY
		else -> emptyArray()
	}

	override fun getHighlightingLexer() = CovLexerAdapter()
}

class CovSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = CovSyntaxHighlighter()
}
