package org.covscript.lang

import com.intellij.CommonBundle
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.*
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*

object CovLanguage : Language(CovBundle.message("cov.name"), "text/$COV_EXTENSION")

object CovFileType : LanguageFileType(CovLanguage) {
	override fun getDefaultExtension() = COV_EXTENSION
	override fun getName() = CovBundle.message("cov.name")
	override fun getIcon() = COV_ICON
	override fun getDescription() = CovBundle.message("cov.name.description")
}

class CovFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, CovLanguage) {
	override fun getFileType() = CovFileType
}

class CovFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) = consumer.consume(CovFileType, COV_EXTENSION)
}

class CovContext : TemplateContextType(CovBundle.message("cov.name"), CovBundle.message("cov.name")) {
	override fun isInContext(file: PsiFile, p1: Int) = file.name.endsWith(".$COV_EXTENSION")
}

class CovLiveTemplateProvider : DefaultLiveTemplatesProvider {
	override fun getDefaultLiveTemplateFiles() = arrayOf("liveTemplates/CovScript")
	override fun getHiddenLiveTemplateFiles() = null
}

object CovPackageFileType : LanguageFileType(CovLanguage) {
	override fun getDefaultExtension() = COV_PKG_EXTENSION
	override fun getIcon() = COV_PKG_ICON
	override fun getName() = CovBundle.message("cov.package.name")
	override fun getDescription() = CovBundle.message("cov.package.name.description")
}

object CovExtensionFileType : FileType {
	override fun getDefaultExtension() = COV_EXT_EXTENSION
	override fun getIcon() = COV_EXT_ICON
	override fun getCharset(file: VirtualFile, bytes: ByteArray) = null
	override fun getName() = CovBundle.message("cov.extension.name")
	override fun getDescription() = CovBundle.message("cov.extension.name.description")
	override fun isBinary() = true
	override fun isReadOnly() = true
}

class CovPackageFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) = consumer.consume(CovPackageFileType, COV_PKG_EXTENSION)
}

class CovExtensionFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) = consumer.consume(CovExtensionFileType, COV_EXT_EXTENSION)
}


object CovBundle {
	@NonNls private const val BUNDLE = "org.covscript.lang.cov-bundle"
	private val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(BUNDLE) }

	@JvmStatic
	fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
			CommonBundle.message(bundle, key, *params)
}

