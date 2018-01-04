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
		@JvmField val KEYWORD = TextAttributesKey.createTextAttributesKey("COV_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
		@JvmField val NUMBER = TextAttributesKey.createTextAttributesKey("COV_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
		@JvmField val COMMENT = TextAttributesKey.createTextAttributesKey("COV_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
		private val KEYWORD_KEY = arrayOf(KEYWORD)
		private val COMMENT_KEY = arrayOf(COMMENT)
		private val NUMBER_KEY = arrayOf(NUMBER)
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
				CovTypes.CONTINUE_KEYWORD,
				CovTypes.BLOCK_KEYWORD,
				CovTypes.TO_KEYWORD,
				CovTypes.ITERATE_KEYWORD,
				CovTypes.UNTIL_KEYWORD,
				CovTypes.LOOP_KEYWORD,
				CovTypes.STEP_KEYWORD,
				CovTypes.THROW_KEYWORD,
				CovTypes.TRY_KEYWORD,
				CovTypes.CATCH_KEYWORD,
				CovTypes.STRUCT_KEYWORD,
				CovTypes.SWITCH_KEYWORD,
				CovTypes.CASE_KEYWORD,
				CovTypes.DEFAULT_KEYWORD)
	}

	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		in KEYWORDS_LIST -> KEYWORD_KEY
		CovTypes.LINE_COMMENT -> COMMENT_KEY
		CovTypes.NUM -> NUMBER_KEY
		else -> emptyArray()
	}

	override fun getHighlightingLexer() = CovLexerAdapter()
}

class CovSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = CovSyntaxHighlighter()
}
