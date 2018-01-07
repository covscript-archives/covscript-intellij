package org.covscript.lang.editing

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.covscript.lang.COV_NAME

class CovRemoveCollapsedBlockIntention(val element: PsiElement) : BaseIntentionAction() {
	override fun getText() = "Remove empty collapsed block"
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = COV_NAME
	override fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		// val file = psiFile?.let { PsiManager.getInstance(project).findFile(it.virtualFile) as? CovFile } ?: return
		element.delete()
	}
}

class CovConvertCollapsedBlockToOrdinaryStatementIntention(val element: PsiElement) : BaseIntentionAction() {
	override fun getText() = "Convert collapsed block into ordinary statement"
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
}
