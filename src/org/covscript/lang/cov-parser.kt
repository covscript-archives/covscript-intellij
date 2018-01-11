package org.covscript.lang

import com.intellij.lang.*
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.*
import org.covscript.lang.psi.CovTokenType
import org.covscript.lang.psi.CovTypes

class CovParserDefinition : ParserDefinition {
	private companion object {
		private val FILE = IFileElementType(Language.findInstance(CovLanguage::class.java))
	}

	override fun createFile(viewProvider: FileViewProvider) = CovFile(viewProvider)
	override fun createParser(project: Project?) = CovParser()
	override fun spaceExistanceTypeBetweenTokens(p0: ASTNode?, p1: ASTNode?) = ParserDefinition.SpaceRequirements.MAY
	override fun createLexer(project: Project?) = CovLexerAdapter()
	override fun getFileNodeType() = FILE
	override fun createElement(astNode: ASTNode?): PsiElement = CovTypes.Factory.createElement(astNode)
	override fun getStringLiteralElements() = CovTokenType.STRINGS
	override fun getCommentTokens() = CovTokenType.COMMENTS
	override fun getWhitespaceTokens(): TokenSet = TokenSet.WHITE_SPACE
}

