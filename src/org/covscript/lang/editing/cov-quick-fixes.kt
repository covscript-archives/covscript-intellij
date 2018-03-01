package org.covscript.lang.editing

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.covscript.lang.CovBundle
import org.covscript.lang.CovTokenType
import org.covscript.lang.psi.CovBlockStatement
import org.covscript.lang.psi.CovCollapsedStatement
import org.covscript.lang.psi.impl.anythingInside
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls

class CovRemoveElementIntention(private val element: PsiElement, @Nls private val intentionText: String) :
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
				element.anythingInside?.text?.replace("\n", "") ?: element.text,
				CovBundle.message("cov.lint.convert-collapsed-block"))

class CovReplaceWithTextIntention(
		private val element: PsiElement,
		@NonNls private val new: String,
		@Nls private val info: String) : BaseIntentionAction() {
	override fun getText() = info
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = CovBundle.message("cov.name")
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		CovTokenType.fromText(new, project).let(element::replace)
	}
}

class CovReplaceWithElementIntention(
		private val element: PsiElement,
		private val new: PsiElement,
		@Nls private val info: String) : BaseIntentionAction() {
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
		element.bodyOfSomething.statementList.firstOrNull()?.let(element::replace) ?: element.delete()
	}
}
