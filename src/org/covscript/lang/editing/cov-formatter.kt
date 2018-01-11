package org.covscript.lang.editing

import com.intellij.formatting.*
import com.intellij.formatting.templateLanguages.BlockWithParent
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.FormatterUtil
import com.intellij.psi.formatter.common.AbstractBlock
import org.covscript.lang.CovLanguage


class CovFormattingModelBuilder : FormattingModelBuilder {
	override fun getRangeAffectingIndent(psiFile: PsiFile, p1: Int, astNode: ASTNode) = null
	override fun createModel(element: PsiElement, settings: CodeStyleSettings): FormattingModel {
		val file = element.containingFile.viewProvider.getPsi(CovLanguage)
		return FormattingModelProvider.createFormattingModelForPsiFile(file,
				CovBlock(file.node, settings), settings)
	}
}

fun isWhitespaceOrComment(element: PsiElement) = element is PsiWhiteSpace || element is PsiComment
fun getPrevSiblingSkipWhiteSpacesAndComments(sibling: ASTNode): ASTNode? {
	var result: ASTNode? = sibling.treePrev
	while (result != null && isWhitespaceOrComment(result.psi)) result = result.treePrev
	return result
}

class CovBlock(
		node: ASTNode,
		private val settings: CodeStyleSettings,
		wrap: Wrap? = null,
		alignment: Alignment? = null) : AbstractBlock(node, wrap, alignment), BlockWithParent {
	private var parent: BlockWithParent? = null
	override fun getParent() = parent
	override fun setParent(withParent: BlockWithParent) {
		parent = withParent
	}

	override fun isLeaf() = false
	override fun getSpacing(child1: Block?, child2: Block) = null
	override fun buildChildren(): MutableList<Block> {
		if (isLeaf) return AbstractBlock.EMPTY
		val list = arrayListOf<Block>()
		var childNode = node.firstChildNode
		while (childNode != null) {
			if (FormatterUtil.containsWhiteSpacesOnly(childNode)) {
				childNode = childNode.treeNext
				continue
			}
			val childBlock = CovBlock(childNode, settings)
			childBlock.parent = this
			list.add(childBlock)
			childNode = childNode.treeNext
		}
		return list
	}

	override fun getIndent() = indent
	private val indent: Indent = let {
		Indent.getNormalIndent()
	}
}
