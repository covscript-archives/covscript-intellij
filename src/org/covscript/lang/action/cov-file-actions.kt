package org.covscript.lang.action

import com.intellij.CommonBundle
import com.intellij.ide.actions.CreateFileAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.*
import org.covscript.lang.*
import java.time.LocalDate

class NewCovFile : CreateFileAction(
		CovBundle.message("cov.actions.new-file.title"),
		CovBundle.message("cov.actions.new-file.description"),
		COV_ICON), DumbAware {
	private companion object Caption {
		private val initCode
			get() = """#
# ${CovBundle.message("cov.actions.new-file.content", System.getProperty("user.name"), LocalDate.now())}
#

"""
	}

	override fun getActionName(directory: PsiDirectory?, s: String?) =
			CovBundle.message("cov.actions.new-file.title")

	override fun getErrorTitle(): String = CommonBundle.getErrorTitle()
	override fun getDefaultExtension() = COV_EXTENSION
	override fun create(name: String, directory: PsiDirectory): Array<PsiElement> {
		val fixedExtension = when (FileUtilRt.getExtension(name)) {
			COV_EXTENSION -> name
			else -> "$name.$COV_EXTENSION"
		}
		return arrayOf(directory.add(PsiFileFactory
				.getInstance(directory.project)
				.createFileFromText(fixedExtension, CovFileType, initCode)))
	}
}

