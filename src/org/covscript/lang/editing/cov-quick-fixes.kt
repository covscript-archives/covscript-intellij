package org.covscript.lang.editing

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import org.covscript.lang.*
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

class CovCollapsedBlockToOneStatementIntention(
		private val element: CovCollapsedStatement) : BaseIntentionAction() {
	override fun getText() = CovBundle.message("cov.lint.convert-collapsed-block")
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = CovBundle.message("cov.name")
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		val statement = element.anythingInside ?: return
		element.replace(PsiFileFactory
				.getInstance(project)
				.createFileFromText(CovLanguage, statement.text.replace("\n", ""))
				.let { it as? CovFile }
				?.firstChild ?: return)
	}
}

class CovReplaceWithTextIntention(
		private val element: PsiElement,
		private val new: String,
		private val info: String) : BaseIntentionAction() {
	override fun getText() = info
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = CovBundle.message("cov.name")
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		element.replace(PsiFileFactory
				.getInstance(project)
				.createFileFromText(CovLanguage, new)
				.let { it as? CovFile }
				?.firstChild ?: return)
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
