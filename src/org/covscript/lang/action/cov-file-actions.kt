package org.covscript.lang.action

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.actions.AttributesDefaults
import com.intellij.ide.fileTemplates.ui.CreateFromTemplateDialog
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.PsiDirectory
import icons.CovIcons
import org.covscript.lang.CovBundle
import org.covscript.lang.editing.CovFileNameValidator
import org.covscript.lang.module.covSettings
import java.util.*

class NewCovFile : CreateFileFromTemplateAction(
		CovBundle.message("cov.actions.new-file.title"),
		CovBundle.message("cov.actions.new-file.description"),
		CovIcons.COV_ICON), DumbAware {
	companion object {
		fun createProperties(project: Project, className: String): Properties {
			val settings = project.covSettings.settings
			val properties = FileTemplateManager.getInstance(project).defaultProperties
			properties += "COV_VERSION" to settings.version
			properties += "NAME" to className
			return properties
		}
	}

	override fun getActionName(directory: PsiDirectory, s: String, s2: String) =
			CovBundle.message("cov.actions.new-file.title")

	override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
		builder
				.setTitle(CovBundle.message("cov.actions.new-file.title"))
				.setValidator(CovFileNameValidator)
				.addKind("File", CovIcons.COV_ICON, "CovScript File")
				.addKind("Namespace", CovIcons.NAMESPACE_ICON, "CovScript Namespace")
				.addKind("Struct", CovIcons.STRUCT_ICON, "CovScript Struct")
	}

	override fun createFileFromTemplate(name: String, template: FileTemplate, dir: PsiDirectory) = try {
		val className = FileUtilRt.getNameWithoutExtension(name)
		val project = dir.project
		val properties = createProperties(project, className)
		CreateFromTemplateDialog(
				project,
				dir,
				template,
				AttributesDefaults(className).withFixedName(true),
				properties)
				.create()
				.containingFile
	} catch (e: Exception) {
		LOG.error("Error while creating new file", e)
		null
	}
}
