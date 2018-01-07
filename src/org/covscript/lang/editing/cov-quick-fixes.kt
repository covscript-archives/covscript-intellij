package org.covscript.lang.editing

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import org.covscript.lang.*
import org.covscript.lang.psi.CovCollapsedStatement

class CovRemoveCollapsedBlockIntention(val element: PsiElement) : BaseIntentionAction() {
	override fun getText() = "Remove empty collapsed block"
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = COV_NAME
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		// val file = psiFile?.let { PsiManager.getInstance(project).findFile(it.virtualFile) as? CovFile } ?: return
		element.delete()
	}
}

class CovConvertCollapsedBlockToOrdinaryStatementIntention(val element: CovCollapsedStatement) : BaseIntentionAction() {
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