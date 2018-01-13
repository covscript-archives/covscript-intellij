//package org.covscript.lang.editing
//
//import com.intellij.formatting.*
//import com.intellij.formatting.Indent
//import com.intellij.lang.ASTNode
//import com.intellij.openapi.util.TextRange
//import com.intellij.psi.*
//import com.intellij.psi.codeStyle.*
//import com.intellij.psi.formatter.FormatterUtil
//import com.intellij.psi.formatter.common.AbstractBlock
//import org.covscript.lang.CovLanguage
//
//class CovCodeStyleSettings(container: CodeStyleSettings?) :
//		CustomCodeStyleSettings("CovCodeStyleSettings", container) {
//}
//
//class Context(
//		common: CommonCodeStyleSettings,
//		val custom: CovCodeStyleSettings,
//		val spacingBuilder: SpacingBuilder) {
//	val SHORT_ENOUGH = common.rootSettings.getRightMargin(CovLanguage)
//	val SHORT_REALLY = common.rootSettings.getRightMargin(CovLanguage) / 4
//	val newLineTop = Spacing.createSpacing(1, 1, 2, common.KEEP_LINE_BREAKS, common.KEEP_BLANK_LINES_IN_DECLARATIONS)
//	val newLine = Spacing.createSpacing(1, 1, 1, common.KEEP_LINE_BREAKS, common.KEEP_BLANK_LINES_IN_CODE)
//	val noSpaces = Spacing.createSpacing(0, 0, 0, common.KEEP_LINE_BREAKS, common.KEEP_BLANK_LINES_IN_CODE)
//	val spaceOnly = Spacing.createSpacing(1, 0, 0, false, 0)
//	val spaceOrKeepNL = Spacing.createSpacing(1, 0, 0, common.KEEP_LINE_BREAKS, common.KEEP_BLANK_LINES_IN_CODE)
//}
//
//class CovFormattingModelBuilder : FormattingModelBuilder {
//	override fun getRangeAffectingIndent(psiFile: PsiFile, p1: Int, astNode: ASTNode) = null
//	override fun createModel(element: PsiElement, settings: CodeStyleSettings): FormattingModel {
//		val file = element.containingFile.viewProvider.getPsi(CovLanguage)
//		return FormattingModelProvider.createFormattingModelForPsiFile(file,
//				CovBlock(file.node, settings, Indent.getAbsoluteNoneIndent()), settings)
//	}
//}
//
//fun isWhitespaceOrComment(element: PsiElement) = element is PsiWhiteSpace || element is PsiComment
//fun getPrevSiblingSkipWhiteSpacesAndComments(sibling: ASTNode): ASTNode? {
//	var result: ASTNode? = sibling.treePrev
//	while (result != null && isWhitespaceOrComment(result.psi)) result = result.treePrev
//	return result
//}
//
//class CovBlock(
//		node: ASTNode,
//		private val settings: CodeStyleSettings,
//		private val indent: Indent,
//		wrap: Wrap? = null,
//		alignment: Alignment? = null) : AbstractBlock(node, wrap, alignment) {
//	private val childAlignment = Alignment.createAlignment()
//	private val childrenBlocks by lazy { childrenBlocks() }
//	override fun isLeaf() = node.firstChildNode == null
//	override fun getSpacing(child1: Block?, child2: Block) = null
//	override fun getTextRange(): TextRange = node.textRange
//	override fun buildChildren() = childrenBlocks
//	private fun childrenBlocks(): MutableList<Block> {
//		if (isLeaf) return AbstractBlock.EMPTY
//		val list = arrayListOf<Block>()
//		var childNode = node.firstChildNode
//		while (childNode != null) {
//			if (FormatterUtil.containsWhiteSpacesOnly(childNode)) {
//				childNode = childNode.treeNext
//				continue
//			}
//			val childBlock = CovBlock(childNode, settings, indent)
//			list.add(childBlock)
//			childNode = childNode.treeNext
//		}
//		return list
//	}
//
//	override fun getIndent() = indent
//}
