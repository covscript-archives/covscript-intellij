package org.covscript.lang.editing

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import org.covscript.lang.CovBundle
import org.covscript.lang.CovLanguage
import org.covscript.lang.psi.CovBlockStatement
import org.covscript.lang.psi.CovCollapsedStatement
import org.covscript.lang.psi.impl.anythingInside

class CovRemoveElementIntention(private val element: PsiElement, private val intentionText: String) :
		BaseIntentionAction() {
	override fun getText() = intentionText
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = CovBundle.message("cov.name")
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		element.delete()
	}
}

fun collapsedToOneLine(element: CovCollapsedStatement) =
		CovReplaceWithTextIntention(
				element,
				CovBundle.message("cov.lint.convert-collapsed-block"),
				element.anythingInside?.text?.replace("\n", "") ?: element.text
		)

class CovReplaceWithTextIntention(
		private val element: PsiElement,
		private val new: String,
		private val info: String) : BaseIntentionAction() {
	override fun getText() = info
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = CovBundle.message("cov.name")
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		PsiFileFactory
				.getInstance(project)
				.createFileFromText(CovLanguage, new)
				?.firstChild
				?.let(element::replace)
	}
}

class CovReplaceWithElementIntention(
		private val element: PsiElement,
		private val new: PsiElement,
		private val info: String) : BaseIntentionAction() {
	override fun getText() = info
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = CovBundle.message("cov.name")
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		element.replace(new)
	}
}

class CovBlockToStatementIntention(
		private val element: CovBlockStatement) : BaseIntentionAction() {
	override fun getText() = CovBundle.message("cov.lint.remove-unnecessary-block")
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = CovBundle.message("cov.name")
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		if (element.bodyOfSomething.statementList.isEmpty()) {
			element.delete()
			return
		}
		element.replace(element.bodyOfSomething.statementList.firstOrNull() ?: return)
	}
}
