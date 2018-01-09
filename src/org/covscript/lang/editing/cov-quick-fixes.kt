package org.covscript.lang.editing

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import org.covscript.lang.*
import org.covscript.lang.psi.CovCollapsedStatement

class CovRemoveBlockIntention(private val element: PsiElement, private val intentionText: String) : BaseIntentionAction() {
	override fun getText() = intentionText
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = COV_NAME
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		element.delete()
	}
}

class CovConvertCollapsedBlockToOrdinaryStatementIntention(
		private val element: CovCollapsedStatement) : BaseIntentionAction() {
	override fun getText() = "Convert collapsed block into ordinary statement"
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = COV_NAME
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		val statement = element.primaryStatement ?: return
		val newStatement = PsiFileFactory
				.getInstance(project)
				.createFileFromText(CovLanguage, statement.text.replace("\n", ""))
				.let { it as? CovFile }
				?.firstChild ?: return
		element.replace(newStatement)
	}
}
