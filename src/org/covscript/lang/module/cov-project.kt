package org.covscript.lang.module

import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModifiableModelsProvider
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.DirectoryProjectGenerator
import com.intellij.platform.DirectoryProjectGeneratorBase
import icons.CovIcons
import org.covscript.lang.CovBundle
import java.nio.file.Files
import java.nio.file.Paths

class CovProjectGenerator : DirectoryProjectGeneratorBase<CovSettings>(),
		CustomStepProjectGenerator<CovSettings> {
	override fun createStep(
			projectGenerator: DirectoryProjectGenerator<CovSettings>,
			callback: AbstractNewProjectStep.AbstractCallback<CovSettings>) =
			ProjectSettingsStepBase(projectGenerator, AbstractNewProjectStep.AbstractCallback<CovSettings>())

	override fun getLogo() = CovIcons.COV_BIG_ICON
	override fun getName() = CovBundle.message("cov.name")
	override fun createPeer() = CovProjectGeneratorPeerImpl(CovSettings())

	override fun generateProject(project: Project, baseDir: VirtualFile, settings: CovSettings, module: Module) {
		ApplicationManager.getApplication().runWriteAction {
			val modifiableModel = ModifiableModelsProvider.SERVICE.getInstance().getModuleModifiableModel(module)
			modifiableModel.inheritSdk()
			val srcPath = Paths.get(baseDir.path, "src").toAbsolutePath()
			Files.createDirectories(srcPath)
			ModifiableModelsProvider.SERVICE.getInstance()
					.commitModuleModifiableModel(modifiableModel)
		}
	}
}