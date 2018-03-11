package org.covscript.lang.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import icons.CovIcons
import org.covscript.lang.CovBundle
import org.covscript.lang.module.*
import java.nio.file.Paths

class StartCovPkgAction : AnAction(
		CovBundle.message("cov.actions.pkg.gui.name"),
		CovBundle.message("cov.actions.pkg.gui.description"),
		CovIcons.COV_BIG_ICON), DumbAware {
	override fun actionPerformed(event: AnActionEvent) {
		val settings = event.project?.covSettings?.settings ?: return
		val cspkgPath = Paths.get(Paths.get(settings.exePath).parent.toString(), "cspkg")
		Runtime.getRuntime().exec("$cspkgPath --gui")
				.waitFor()
	}

	override fun update(event: AnActionEvent) {
		event.presentation.isEnabledAndVisible = event.project?.covSettings?.let { validateCovExe(it.settings) } ?: false
	}
}

