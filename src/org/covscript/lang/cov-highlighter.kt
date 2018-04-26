package org.covscript.lang

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.options.colors.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import icons.CovIcons
import org.covscript.lang.psi.CovTypes
import org.intellij.lang.annotations.Language

class CovSyntaxHighlighter : SyntaxHighlighter {
	companion object {
		@JvmField val KEYWORD = TextAttributesKey.createTextAttributesKey("COV_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
		@JvmField val NUMBER = TextAttributesKey.createTextAttributesKey("COV_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
		@JvmField val COMMENT = TextAttributesKey.createTextAttributesKey("COV_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
		@JvmField val OPERATOR = TextAttributesKey.createTextAttributesKey("COV_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
		@JvmField val STRING = TextAttributesKey.createTextAttributesKey("COV_STRING", DefaultLanguageHighlighterColors.STRING)
		@JvmField val STRING_ESCAPE = TextAttributesKey.createTextAttributesKey("COV_STRING_ESCAPE", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE)
		@JvmField val BEGIN_END_BLOCK = TextAttributesKey.createTextAttributesKey("COV_BEGIN_END_BLOCK", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR)
		@JvmField val BEGIN_END_THEMSELVES = TextAttributesKey.createTextAttributesKey("COV_BEGIN_END_THEMSELVES", HighlighterColors.TEXT)
		@JvmField val FUNCTION_DEFINITION = TextAttributesKey.createTextAttributesKey("COV_FUNCTION_DEFINITION", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
		@JvmField val NAMESPACE_DEFINITION = TextAttributesKey.createTextAttributesKey("COV_NAMESPACE_DEFINITION", DefaultLanguageHighlighterColors.CLASS_NAME)
		@JvmField val VARIABLE_DEFINITION = TextAttributesKey.createTextAttributesKey("COV_VARIABLE_DEFINITION", DefaultLanguageHighlighterColors.LOCAL_VARIABLE)
		@JvmField val STRUCT_DEFINITION = TextAttributesKey.createTextAttributesKey("COV_STRUCT_DEFINITION", DefaultLanguageHighlighterColors.CLASS_NAME)
		@JvmField val CONST_DEFINITION = TextAttributesKey.createTextAttributesKey("COV_CONST_DEFINITION", DefaultLanguageHighlighterColors.CLASS_NAME)
		@JvmField val FUNCTION_REFERENCE = TextAttributesKey.createTextAttributesKey("COV_FUNCTION_REFERENCE", DefaultLanguageHighlighterColors.FUNCTION_CALL)
		@JvmField val NAMESPACE_REFERENCE = TextAttributesKey.createTextAttributesKey("COV_NAMESPACE_REFERENCE", DefaultLanguageHighlighterColors.CLASS_REFERENCE)
		@JvmField val VARIABLE_REFERENCE = TextAttributesKey.createTextAttributesKey("COV_VARIABLE_REFERENCE", DefaultLanguageHighlighterColors.IDENTIFIER)
		@JvmField val STRUCT_REFERENCE = TextAttributesKey.createTextAttributesKey("COV_STRUCT_REFERENCE", DefaultLanguageHighlighterColors.CLASS_REFERENCE)
		@JvmField val CONST_REFERENCE = TextAttributesKey.createTextAttributesKey("COV_CONST_REFERENCE", DefaultLanguageHighlighterColors.CONSTANT)
		private val KEYWORD_KEY = arrayOf(KEYWORD)
		private val COMMENT_KEY = arrayOf(COMMENT)
		private val NUMBER_KEY = arrayOf(NUMBER)
		private val STRING_KEY = arrayOf(STRING)
		private val OPERATOR_KEY = arrayOf(OPERATOR)
		private val BEGIN_END_THEMSELVES_KEY = arrayOf(BEGIN_END_THEMSELVES)
		private val KEYWORDS_LIST = listOf(
				CovTypes.IF_KEYWORD,
				CovTypes.ELSE_KEYWORD,
				CovTypes.END_KEYWORD,
				CovTypes.NEW_KEYWORD,
				CovTypes.GCNEW_KEYWORD,
				CovTypes.TYPEID_KEYWORD,
				CovTypes.WHILE_KEYWORD,
				CovTypes.FOR_KEYWORD,
				CovTypes.PACKAGE_KEYWORD,
				CovTypes.USING_KEYWORD,
				CovTypes.TRUE_KEYWORD,
				CovTypes.FALSE_KEYWORD,
				CovTypes.NULL_KEYWORD,
				CovTypes.IMPORT_KEYWORD,
				CovTypes.VAR_KEYWORD,
				CovTypes.CONST_KEYWORD,
				CovTypes.NAMESPACE_KEYWORD,
				CovTypes.FUNCTION_KEYWORD,
				CovTypes.BREAK_KEYWORD,
				CovTypes.CONTINUE_KEYWORD,
				CovTypes.RETURN_KEYWORD,
				CovTypes.BLOCK_KEYWORD,
				CovTypes.TO_KEYWORD,
				CovTypes.ITERATE_KEYWORD,
				CovTypes.UNTIL_KEYWORD,
				CovTypes.LOOP_KEYWORD,
				CovTypes.STEP_KEYWORD,
				CovTypes.THROW_KEYWORD,
				CovTypes.TRY_KEYWORD,
				CovTypes.CATCH_KEYWORD,
				CovTypes.STRUCT_KEYWORD,
				CovTypes.SWITCH_KEYWORD,
				CovTypes.CASE_KEYWORD,
				CovTypes.DEFAULT_KEYWORD,
				CovTypes.NOT_KEYWORD,
				CovTypes.AND_KEYWORD,
				CovTypes.OR_KEYWORD,
				CovTypes.OVERRIDE_KEYWORD,
				CovTypes.EXTENDS_KEYWORD
		)

		@JvmField val OPERATOR_LIST = listOf(
				CovTypes.QUESTION_SYM,
				CovTypes.COLON_SYM,
				CovTypes.DIV_ASS,
				CovTypes.PLUS_ASS,
				CovTypes.MINUS_ASS,
				CovTypes.TIMES_ASS,
				CovTypes.POW_ASS,
				CovTypes.REM_ASS,
				CovTypes.QUESTION_SYM,
				CovTypes.PLUS_SYM,
				CovTypes.MINUS_SYM,
				CovTypes.TIMES_SYM,
				CovTypes.DIV_SYM,
				CovTypes.REM_SYM,
				CovTypes.POW_SYM,
				CovTypes.COLON_SYM,
				CovTypes.NOT_SYM,
				CovTypes.AND_SYM,
				CovTypes.OR_SYM,
				CovTypes.LT_SYM,
				CovTypes.GT_SYM,
				CovTypes.EQ_SYM,
				CovTypes.LE_SYM,
				CovTypes.GE_SYM,
				CovTypes.UN_SYM
		)
	}

	override fun getHighlightingLexer() = CovLexerAdapter()
	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		CovTokenType.LINE_COMMENT -> COMMENT_KEY
		CovTypes.NUM -> NUMBER_KEY
		CovTypes.STR, CovTypes.CHAR -> STRING_KEY
		CovTypes.COLLAPSER_BEGIN, CovTypes.COLLAPSER_END -> BEGIN_END_THEMSELVES_KEY
		in KEYWORDS_LIST -> KEYWORD_KEY
		in OPERATOR_LIST -> OPERATOR_KEY
		else -> emptyArray()
	}
}

class CovSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = CovSyntaxHighlighter()
}

class CovColorSettingsPage : ColorSettingsPage {
	companion object {
		private val DESCRIPTORS = arrayOf(
				AttributesDescriptor(CovBundle.message("cov.settings.color.string.string"), CovSyntaxHighlighter.STRING),
				AttributesDescriptor(CovBundle.message("cov.settings.color.string.escape"), CovSyntaxHighlighter.STRING_ESCAPE),
				AttributesDescriptor(CovBundle.message("cov.settings.color.keywords.common"), CovSyntaxHighlighter.KEYWORD),
				AttributesDescriptor(CovBundle.message("cov.settings.color.keywords.collapsed"), CovSyntaxHighlighter.BEGIN_END_THEMSELVES),
				AttributesDescriptor(CovBundle.message("cov.settings.color.collapsed"), CovSyntaxHighlighter.BEGIN_END_BLOCK),
				AttributesDescriptor(CovBundle.message("cov.settings.color.comment"), CovSyntaxHighlighter.COMMENT),
				AttributesDescriptor(CovBundle.message("cov.settings.color.number"), CovSyntaxHighlighter.NUMBER),
				AttributesDescriptor(CovBundle.message("cov.settings.color.declarations.function"), CovSyntaxHighlighter.FUNCTION_DEFINITION),
				AttributesDescriptor(CovBundle.message("cov.settings.color.declarations.namespace"), CovSyntaxHighlighter.NAMESPACE_DEFINITION),
				AttributesDescriptor(CovBundle.message("cov.settings.color.declarations.variable"), CovSyntaxHighlighter.VARIABLE_DEFINITION),
				AttributesDescriptor(CovBundle.message("cov.settings.color.declarations.struct"), CovSyntaxHighlighter.STRUCT_DEFINITION),
				AttributesDescriptor(CovBundle.message("cov.settings.color.declarations.const"), CovSyntaxHighlighter.CONST_DEFINITION),
				AttributesDescriptor(CovBundle.message("cov.settings.color.references.function"), CovSyntaxHighlighter.FUNCTION_REFERENCE),
				AttributesDescriptor(CovBundle.message("cov.settings.color.references.namespace"), CovSyntaxHighlighter.NAMESPACE_REFERENCE),
				AttributesDescriptor(CovBundle.message("cov.settings.color.references.variable"), CovSyntaxHighlighter.VARIABLE_REFERENCE),
				AttributesDescriptor(CovBundle.message("cov.settings.color.references.struct"), CovSyntaxHighlighter.STRUCT_REFERENCE),
				AttributesDescriptor(CovBundle.message("cov.settings.color.references.const"), CovSyntaxHighlighter.CONST_REFERENCE),
				AttributesDescriptor(CovBundle.message("cov.settings.color.operators"), CovSyntaxHighlighter.OPERATOR)
		)
		private val KEYS = mapOf(
				"beginEndBlock" to CovSyntaxHighlighter.BEGIN_END_BLOCK,
				"escapeCharacter" to CovSyntaxHighlighter.STRING_ESCAPE,
				"functionName" to CovSyntaxHighlighter.FUNCTION_DEFINITION,
				"structName" to CovSyntaxHighlighter.STRUCT_DEFINITION,
				"namespaceName" to CovSyntaxHighlighter.NAMESPACE_DEFINITION,
				"constantName" to CovSyntaxHighlighter.CONST_DEFINITION,
				"variableName" to CovSyntaxHighlighter.VARIABLE_DEFINITION,
				"variableRef" to CovSyntaxHighlighter.VARIABLE_REFERENCE,
				"functionRef" to CovSyntaxHighlighter.FUNCTION_REFERENCE,
				"namespaceRef" to CovSyntaxHighlighter.NAMESPACE_REFERENCE,
				"constRef" to CovSyntaxHighlighter.VARIABLE_REFERENCE,
				"structRef" to CovSyntaxHighlighter.STRUCT_REFERENCE
		)
	}

	override fun getHighlighter() = CovSyntaxHighlighter()
	override fun getIcon() = CovIcons.COV_ICON
	override fun getDisplayName() = CovBundle.message("cov.name")
	override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
	override fun getAdditionalHighlightingTagToDescriptorMap() = KEYS
	override fun getAttributeDescriptors() = DESCRIPTORS

	@Language("CovScript")
	override fun getDemoText() = """# CovScript code example
namespace <namespaceName>std</namespaceName>
  struct <structName>MyStruct</structName>
  end
  const var <constantName>ThisIsAType</constantName> = typeid <structRef>MyStruct</structRef>
  var <variableName>someString</variableName> = gcnew string
  <variableRef>someString</variableRef> = to_string(<constRef>ThisIsAType</constRef>)

  function <functionName>main</functionName>(args)
    const var <variableName>str</variableName> = "boy next door<escapeCharacter>\n</escapeCharacter>" + to_string(<variableRef>y</variableRef>)
    # @begin and @end are not highlighted as well as their inside part
    # due to the limitation of IntelliJ's preview system
    @begin<beginEndBlock>
      system.out.println(str +
      "")
    </beginEndBlock>@end
  end
end
<namespaceRef>std</namespaceRef>.<functionRef>main</functionRef>({233 * 666, 114514})
"""
}
