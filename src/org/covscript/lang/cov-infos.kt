package org.covscript.lang

import com.intellij.CommonBundle
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.*
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import icons.CovIcons
import org.covscript.lang.psi.impl.processDeclTrivial
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*

object CovPackageFileType : LanguageFileType(CovLanguage.INSTANCE) {
	override fun getDefaultExtension() = COV_PKG_EXTENSION
	override fun getIcon() = CovIcons.COV_PKG_ICON
	override fun getName() = CovBundle.message("cov.package.name")
	override fun getDescription() = CovBundle.message("cov.package.name.description")
}

object CovFileType : LanguageFileType(CovLanguage.INSTANCE) {
	override fun getDefaultExtension() = COV_EXTENSION
	override fun getIcon() = CovIcons.COV_ICON
	override fun getName() = CovBundle.message("cov.name")
	override fun getDescription() = CovBundle.message("cov.name.description")
}

class CovFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, CovLanguage.INSTANCE) {
	override fun getFileType() = CovFileType
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
			processDeclTrivial(processor, state, lastParent, place)
}

class CovFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) {
		consumer.consume(CovPackageFileType, COV_PKG_EXTENSION)
		consumer.consume(CovFileType, COV_EXTENSION)
		consumer.consume(CovExtensionFileType, COV_EXT_EXTENSION)
	}
}

class CovContext : TemplateContextType(
		CovBundle.message("cov.live-templates.statement.id"),
		CovBundle.message("cov.live-templates.statement.name")) {
	override fun isInContext(file: PsiFile, offset: Int) = file.fileType == CovFileType
}

class CovLiveTemplateProvider : DefaultLiveTemplatesProvider {
	override fun getDefaultLiveTemplateFiles() = arrayOf("liveTemplates/CovScript")
	override fun getHiddenLiveTemplateFiles() = emptyArray<String>()
}

object CovExtensionFileType : FileType {
	override fun getDefaultExtension() = COV_EXT_EXTENSION
	override fun getIcon() = CovIcons.COV_EXT_ICON
	override fun getCharset(file: VirtualFile, bytes: ByteArray): String? = null
	override fun getName() = CovBundle.message("cov.extension.name")
	override fun getDescription() = CovBundle.message("cov.extension.name.description")
	override fun isBinary() = true
	override fun isReadOnly() = true
}


object CovBundle {
	@NonNls private const val BUNDLE = "org.covscript.lang.cov-bundle"
	private val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(BUNDLE) }

	@JvmStatic
	fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
			CommonBundle.message(bundle, key, *params)
}

