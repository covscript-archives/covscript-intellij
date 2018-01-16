package org.covscript.lang.action

import com.google.common.util.concurrent.UncheckedTimeoutException
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import com.intellij.ui.ScrollPaneFactory
import com.intellij.util.ui.JBUI
import org.covscript.lang.COV_BIG_ICON
import org.covscript.lang.module.executeInRepl
import org.covscript.lang.module.projectSdk
import java.awt.Dimension
import javax.swing.JLabel
import javax.swing.JTextArea

private class InvalidCovSdkException(val path: String) : RuntimeException()
class TryEvaluate {
	private var textLimit = 360
	private var timeLimit = 1500L
	private var builder = StringBuilder()

	private fun StringBuilder.insertOutputIfNonBlank() = insert(0, if (isNotBlank()) "\nOutput:\n" else "")
	fun tryEval(editor: Editor, text: String, project: Project?, popupWhenSuccess: Boolean) {
		if (builder.isNotBlank()) builder = StringBuilder()
		try {
			var covRoot = ""
			var covVersion = ""
			project?.projectSdk?.let {
				covRoot = it.homePath.orEmpty()
				covVersion = it.versionString.orEmpty()
			}
			executeInRepl(covRoot, editor.selectionModel.selectedText ?: return, timeLimit)
			builder.insertOutputIfNonBlank()
			builder.insert(0, "Execution output under CovScript $covVersion")
			if (popupWhenSuccess) showPopupWindow(builder.toString(), editor, 0x0013F9, 0x000CA1)
		} catch (e: UncheckedTimeoutException) {
			builder.insertOutputIfNonBlank()
			builder.insert(0, "Execution timeout.\nChange time limit in Project Structure | Facets")
			showPopupWindow(builder.toString(), editor, 0xEDC209, 0xC26500)
		} catch (e: Throwable) {
			val cause = e as? InvalidCovSdkException ?: e.cause as? InvalidCovSdkException
			builder.insertOutputIfNonBlank()
			if (cause != null) {
				builder.insert(0, "Invalid CovScript SDK path:\n${cause.path}")
				showPopupWindow(builder.toString(), editor, 0xEDC209, 0xC26500)
			} else {
				builder.insert(0, "Oops! A ${e.javaClass.simpleName} is thrown:\n${e.message}")
				showPopupWindow(builder.toString(), editor, 0xE20911, 0xC20022)
			}
		}
	}

	fun showPopupWindow(
			result: String,
			editor: Editor,
			color: Int,
			colorDark: Int) {
		val relativePoint = JBPopupFactory.getInstance().guessBestPopupLocation(editor)
		if (result.length < textLimit)
			ApplicationManager.getApplication().invokeLater {
				JBPopupFactory.getInstance()
						.createHtmlTextBalloonBuilder(result, COV_BIG_ICON, JBColor(color, colorDark), null)
						.setFadeoutTime(8000)
						.setHideOnAction(true)
						.createBalloon()
						.show(relativePoint, Balloon.Position.below)
			}
		else
			ApplicationManager.getApplication().invokeLater {
				JBPopupFactory.getInstance()
						.createComponentPopupBuilder(JBUI.Panels.simplePanel()
								.addToTop(JLabel(COV_BIG_ICON))
								.addToCenter(ScrollPaneFactory.createScrollPane(JTextArea(result).also {
									it.toolTipText = "Evaluation output longer than $textLimit characters"
									it.lineWrap = true
									it.wrapStyleWord = true
									it.isEditable = false
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

