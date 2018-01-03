package org.covscript.lang

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.*
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile

object CovLanguage : Language(COV_NAME, "text/$COV_EXTENSION")

object CovFileType : LanguageFileType(CovLanguage) {
	override fun getDefaultExtension() = COV_EXTENSION
	override fun getName() = COV_NAME
	override fun getIcon() = COV_ICON
	override fun getDescription() = COV_NAME
}

class CovFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, CovLanguage) {
	override fun getFileType() = CovFileType
}

class CovFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) = consumer.consume(CovFileType, COV_EXTENSION)
}

class LiceContext : TemplateContextType(COV_NAME, COV_NAME) {
	override fun isInContext(file: PsiFile, p1: Int) = file.name.endsWith(".$COV_EXTENSION")
}

