package org.covscript.lang

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.*
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile

object CovLanguage : Language(COV_NAME, "text/$COV_EXTENSION")

object CovFileType : LanguageFileType(CovLanguage) {
	override fun getDefaultExtension() = COV_EXTENSION
	override fun getName() = COV_NAME
	override fun getIcon() = COV_ICON
	override fun getDescription() = COV_DESCRIPTION
}

class CovFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, CovLanguage) {
	override fun getFileType() = CovFileType
}

class CovFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) = consumer.consume(CovFileType, COV_EXTENSION)
}

class CovContext : TemplateContextType(COV_NAME, COV_NAME) {
	override fun isInContext(file: PsiFile, p1: Int) = file.name.endsWith(".$COV_EXTENSION")
}

object CovPackageFileType : LanguageFileType(CovLanguage) {
	override fun getDefaultExtension() = COV_PKG_EXTENSION
	override fun getIcon() = COV_PKG_ICON
	override fun getName() = COV_PKG_NAME
	override fun getDescription() = COV_PKG_DESCRIPTION
}

object CovExtensionFileType : FileType {
	override fun getDefaultExtension() = COV_EXT_EXTENSION
	override fun getIcon() = COV_EXT_ICON
	override fun getCharset(file: VirtualFile, bytes: ByteArray) = null
	override fun getName() = COV_EXT_NAME
	override fun getDescription() = COV_EXT_DESCRIPTION
	override fun isBinary() = true
	override fun isReadOnly() = true
}

class CovPackageFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) = consumer.consume(CovPackageFileType, COV_PKG_EXTENSION)
}

class CovExtensionFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) = consumer.consume(CovExtensionFileType, COV_EXT_EXTENSION)
}

