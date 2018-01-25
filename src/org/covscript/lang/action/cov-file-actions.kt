package org.covscript.lang.action

import com.intellij.CommonBundle
import com.intellij.ide.actions.CreateFileAction
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.*
import org.covscript.lang.*
import org.covscript.lang.module.CovSdkType
import org.covscript.lang.module.projectSdk
import java.time.LocalDate

class NewCovFile : CreateFileAction(
		CovBundle.message("cov.actions.new-file.title"),
		CovBundle.message("cov.actions.new-file.description"),
		COV_ICON), DumbAware {

	override fun getActionName(directory: PsiDirectory?, s: String?) =
			CovBundle.message("cov.actions.new-file.title")

	override fun getErrorTitle(): String = CommonBundle.getErrorTitle()
	override fun getDefaultExtension() = COV_EXTENSION
	override fun isAvailable(context: DataContext) = context.getData(CommonDataKeys.PROJECT)?.projectSdk?.sdkType is CovSdkType
	override fun create(name: String, directory: PsiDirectory): Array<PsiElement> {
		val fileName = FileUtilRt.getNameWithoutExtension(name)
		val fixedExtension = when (FileUtilRt.getExtension(name)) {
			COV_EXTENSION -> name
			else -> "$name.$COV_EXTENSION"
		}
		return arrayOf(directory.add(PsiFileFactory
				.getInstance(directory.project)
				.createFileFromText(fixedExtension, CovFileType, """#
# ${CovBundle.message("cov.actions.new-file.content", System.getProperty("user.name"), LocalDate.now())}
#

namespace $fileName
end
""")))
	}
}

