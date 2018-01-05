package org.covscript.lang.editing

import com.intellij.lang.*
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.covscript.lang.psi.CovTypes

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
