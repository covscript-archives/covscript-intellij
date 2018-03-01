package org.covscript.lang.action

import com.google.common.util.concurrent.UncheckedTimeoutException
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import com.intellij.ui.ScrollPaneFactory
import com.intellij.util.ui.JBUI
import icons.CovIcons
import org.covscript.lang.CovBundle
import org.covscript.lang.CovFileType
import org.covscript.lang.module.covSettings
import org.covscript.lang.module.executeInRepl
import java.awt.Dimension
import javax.swing.JLabel
import javax.swing.JTextArea

private class InvalidCovSdkException(val path: String) : RuntimeException()
class TryEvaluate {
	private var textLimit = 320
	private var timeLimit = 2500L

	fun tryEval(editor: Editor, text: String, project: Project?) {
		try {
			val builder = StringBuilder()
			var covRoot = ""
			var covVersion = ""
			project?.covSettings?.settings?.let {
				covRoot = it.exePath
				covVersion = it.version
				textLimit = it.tryEvaluateTextLimit
				timeLimit = it.tryEvaluateTimeLimit
			}
			val (stdout, stderr) = executeInRepl(covRoot, text, timeLimit)
			builder.appendln(CovBundle.message("cov.messages.try-eval.version-text", covVersion))
			if (stdout.isNotEmpty()) {
				builder.appendln(CovBundle.message("cov.messages.try-eval.stdout"))
				stdout.forEach { builder.appendln(it) }
			}
			if (stderr.isNotEmpty()) {
				builder.appendln(CovBundle.message("cov.messages.try-eval.stderr"))
				stderr.forEach { builder.appendln(it) }
			}
			if (stderr.isNotEmpty()) showPopupWindow(builder.toString(), editor, 0xE20911, 0xC20022)
			else showPopupWindow(builder.toString(), editor, 0x0013F9, 0x000CA1)
		} catch (e: UncheckedTimeoutException) {
			showPopupWindow(CovBundle.message("cov.messages.try-eval.timeout"), editor, 0xEDC209, 0xC26500)
		} catch (e: Throwable) {
			val cause = e.cause ?: e
			if (cause is InvalidCovSdkException) showPopupWindow(CovBundle.message(
					"cov.messages.try-eval.invalid-path", cause.path), editor, 0xEDC209, 0xC26500)
			else showPopupWindow(CovBundle.message(
					"cov.messages.try-eval.exception", e.javaClass.simpleName, e.message.orEmpty()), editor, 0xE20911, 0xC20022)
		}
	}

	private fun showPopupWindow(result: String, editor: Editor, color: Int, colorDark: Int) {
		val relativePoint = JBPopupFactory.getInstance().guessBestPopupLocation(editor)
		if (result.length < textLimit)
			ApplicationManager.getApplication().invokeLater {
				JBPopupFactory.getInstance()
						.createHtmlTextBalloonBuilder(result, CovIcons.COV_BIG_ICON, JBColor(color, colorDark), null)
						.setFadeoutTime(8000)
						.setHideOnAction(true)
						.createBalloon()
						.show(relativePoint, Balloon.Position.below)
			}
		else
			ApplicationManager.getApplication().invokeLater {
				JBPopupFactory.getInstance()
						.createComponentPopupBuilder(JBUI.Panels.simplePanel()
								.addToTop(JLabel(CovIcons.COV_BIG_ICON))
								.addToCenter(ScrollPaneFactory.createScrollPane(JTextArea(result).apply {
									toolTipText = CovBundle.message("cov.messages.try-eval.overflowed-text", textLimit)
									lineWrap = true
									wrapStyleWord = true
									isEditable = false
								}))
								.apply {
									preferredSize = Dimension(500, 500)
									border = JBUI.Borders.empty(10, 5, 5, 5)
								}, null)
						.setRequestFocus(true)
						.setResizable(true)
						.setMovable(true)
						.setCancelOnClickOutside(true)
						.createPopup()
						.show(relativePoint)
			}
	}
}

class TryEvaluateCovExpressionAction : AnAction(
		CovBundle.message("cov.actions.try-eval.name"),
		CovBundle.message("cov.actions.try-eval.description"),
		CovIcons.COV_BIG_ICON), DumbAware {
	private val core = TryEvaluate()
	override fun actionPerformed(event: AnActionEvent) {
		val editor = event.getData(CommonDataKeys.EDITOR) ?: return
		core.tryEval(editor, editor.selectionModel.selectedText ?: return, event.getData(CommonDataKeys.PROJECT))
	}

	override fun update(event: AnActionEvent) {
		event.presentation.isEnabledAndVisible = event.getData(CommonDataKeys.VIRTUAL_FILE)?.fileType == CovFileType
	}
}
