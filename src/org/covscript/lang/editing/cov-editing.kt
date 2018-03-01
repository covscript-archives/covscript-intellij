package org.covscript.lang.editing

import com.intellij.ide.structureView.*
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.lang.*
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.lang.refactoring.NamesValidator
import com.intellij.navigation.LocationPresentation
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.patterns.*
import com.intellij.pom.PomTargetPsiElement
import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
import com.intellij.refactoring.rename.RenameInputValidator
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.spellchecker.tokenizer.Tokenizer
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider
import com.intellij.util.ProcessingContext
import icons.CovIcons
import org.covscript.lang.*
import org.covscript.lang.psi.*


class CovBraceMatcher : PairedBraceMatcher {
	companion object {
		private val PAIRS = arrayOf(
				BracePair(CovTypes.LEFT_BRACKET, CovTypes.RIGHT_BRACKET, false),
				BracePair(CovTypes.LEFT_B_BRACKET, CovTypes.RIGHT_B_BRACKET, false),
				BracePair(CovTypes.LEFT_S_BRACKET, CovTypes.RIGHT_S_BRACKET, false),
				BracePair(CovTypes.FUNCTION_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.NAMESPACE_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.WHILE_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.FOR_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.IF_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.STRUCT_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.TRY_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.LOOP_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.BLOCK_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.SWITCH_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.CASE_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.DEFAULT_KEYWORD, CovTypes.END_KEYWORD, false),
				BracePair(CovTypes.COLLAPSER_BEGIN, CovTypes.COLLAPSER_END, false))
	}

	override fun getCodeConstructStart(psiFile: PsiFile?, openingBraceOffset: Int) = openingBraceOffset
	override fun isPairedBracesAllowedBeforeType(type: IElementType, iElementType: IElementType?) = true
	override fun getPairs() = PAIRS
}

class CovCommenter : Commenter {
	override fun getCommentedBlockCommentPrefix() = blockCommentPrefix
	override fun getCommentedBlockCommentSuffix() = blockCommentSuffix
	override fun getBlockCommentPrefix(): String? = null
	override fun getBlockCommentSuffix(): String? = null
	override fun getLineCommentPrefix() = "# "
}

class CovSpellCheckingStrategy : SpellcheckingStrategy() {
	override fun getTokenizer(element: PsiElement): Tokenizer<*> = when (element) {
		is CovComment, is CovSymbol -> super.getTokenizer(element)
		is CovString -> super.getTokenizer(element).takeIf { it != EMPTY_TOKENIZER } ?: TEXT_TOKENIZER
		else -> EMPTY_TOKENIZER
	}
}

class CovNamesValidator : NamesValidator, RenameInputValidator {
	companion object : PatternCondition<PsiElement>("") {
		override fun accepts(element: PsiElement, context: ProcessingContext?) =
				(element as? PomTargetPsiElement)?.navigationElement is CovSymbol
	}

	override fun isKeyword(s: String, project: Project?) = s in COV_KEYWORDS
	override fun isInputValid(name: String, ele: PsiElement, context: ProcessingContext) =
			isIdentifier(name, ele.project) && !isKeyword(name, ele.project)

	override fun getPattern(): ElementPattern<out PsiElement> = PlatformPatterns.psiElement().with(Companion)
	override fun isIdentifier(name: String, project: Project?) = name.isNotBlank() and
			name.all { it.isLetterOrDigit() || it == '_' } and
			!name.first().isDigit() and
			!isKeyword(name, project)
}

const val TEXT_MAX = 16
const val LONG_TEXT_MAX = 24
private fun cutText(it: String, textMax: Int) = if (it.length <= textMax) it else "${it.take(textMax)}…"
private val PsiElement.isBlockStructure
	get() = this is CovBlockStatement ||
			this is CovNamespaceDeclaration ||
			this is CovFunctionDeclaration ||
			this is CovStructDeclaration ||
			this is CovForStatement ||
			this is CovCollapsedStatement ||
			this is CovTryCatchStatement ||
			this is CovSwitchStatement ||
			this is CovWhileStatement ||
			this is CovLoopUntilStatement ||
			this is CovBlockStatement ||
			this is CovIfStatement ||
			this is CovArrayLiteral

class CovBreadCrumbProvider : BreadcrumbsProvider {
	override fun getLanguages() = arrayOf(CovLanguage.INSTANCE)
	override fun acceptElement(o: PsiElement) = o.isBlockStructure
	override fun getElementTooltip(o: PsiElement) = when (o) {
		is CovFunctionDeclaration -> "function: <${o.text}>"
		is CovStructDeclaration -> "struct: <${o.text}>"
		is CovNamespaceDeclaration -> "namespace: <${o.text}>"
		else -> null
	}

	override fun getElementInfo(o: PsiElement): String = cutText(when (o) {
		is CovFunctionDeclaration -> o.symbol.text
		is CovStructDeclaration -> o.symbol.text
		is CovNamespaceDeclaration -> o.symbol.text
		is CovForStatement -> "for ${o.symbol.text}"
		is CovArrayLiteral -> "array literal"
		is CovLoopUntilStatement -> "loop ${o.expression}"
		is CovWhileStatement -> "while ${o.expression}"
		is CovTryCatchStatement -> "try catch ${o.symbol}"
		is CovSwitchStatement -> "switch statement"
		is CovCollapsedStatement -> "collapsed block"
		is CovBlockStatement -> "begin block"
		is CovIfStatement -> "if ${o.expression}"
		else -> "??"
	}, TEXT_MAX)
}

class CovFoldingBuilder : FoldingBuilderEx() {
	override fun getPlaceholderText(node: ASTNode): String = node.elementType.let { o ->
		cutText(when (o) {
			CovTypes.FUNCTION_DECLARATION -> "function…"
			CovTypes.STRUCT_DECLARATION -> "struct…"
			CovTypes.NAMESPACE_DECLARATION -> "namespace…"
			CovTypes.FOR_STATEMENT -> "for…"
			CovTypes.LOOP_UNTIL_STATEMENT -> "loop…"
			CovTypes.WHILE_STATEMENT -> "while…"
			CovTypes.TRY_CATCH_STATEMENT -> "try…catch…"
			CovTypes.SWITCH_STATEMENT -> "switch…"
			CovTypes.COLLAPSED_STATEMENT -> "@begin…"
			CovTypes.BLOCK_STATEMENT -> "begin…"
			CovTypes.IF_STATEMENT -> "if…"
			CovTypes.ARRAY_LITERAL -> "{…}"
			else -> "??"
		}, TEXT_MAX)
	}

	override fun isCollapsedByDefault(node: ASTNode) = false
	override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean) = SyntaxTraverser
			.psiTraverser(root)
			.forceDisregardTypes(GeneratedParserUtilBase.DUMMY_BLOCK::equals)
			.traverse()
			.filter(PsiElement::isBlockStructure)
			.map { FoldingDescriptor(it, it.textRange) }
			.toList()
			.toTypedArray()
}

class CovStructureViewFactory : PsiStructureViewFactory {
	override fun getStructureViewBuilder(psiFile: PsiFile) = object : TreeBasedStructureViewBuilder() {
		override fun createStructureViewModel(editor: Editor?) = CovModel(psiFile, editor)
		override fun isRootNodeShown() = true
	}

	private class CovModel(file: PsiFile, editor: Editor?) :
			StructureViewModelBase(file, editor, CovStructureElement(file)),
			StructureViewModel.ElementInfoProvider {
		init {
			withSuitableClasses(
					CovBlockStatement::class.java,
					CovNamespaceDeclaration::class.java,
					CovFunctionDeclaration::class.java,
					CovStructDeclaration::class.java,
					CovForStatement::class.java,
					CovCollapsedStatement::class.java,
					CovTryCatchStatement::class.java,
					CovSwitchStatement::class.java,
					CovWhileStatement::class.java,
					CovLoopUntilStatement::class.java,
					CovBlockStatement::class.java,
					CovIfStatement::class.java,
					CovArrayLiteral::class.java)
		}

		override fun isAlwaysShowsPlus(o: StructureViewTreeElement) = false
		override fun isAlwaysLeaf(o: StructureViewTreeElement) = false
		override fun shouldEnterElement(o: Any?) = true
	}

	private class CovStructureElement(o: PsiElement) :
			PsiTreeElementBase<PsiElement>(o),
			SortableTreeElement,
			LocationPresentation {
		override fun getIcon(open: Boolean) = element.let { o ->
			when (o) {
				is CovFile -> CovIcons.COV_ICON
				is CovFunctionDeclaration -> CovIcons.FUNCTION_ICON
				is CovStructDeclaration -> CovIcons.STRUCT_ICON
				is CovVariableDeclaration -> CovIcons.VARIABLE_ICON
				is CovNamespaceDeclaration -> CovIcons.NAMESPACE_ICON
				is CovTryCatchStatement -> CovIcons.TRY_CATCH_ICON
				is CovBlockStatement -> CovIcons.BLOCK_ICON
				is CovSwitchStatement -> CovIcons.SWITCH_ICON
				is CovCollapsedStatement -> CovIcons.COLLAPSED_ICON
				is CovIfStatement,
				is CovForStatement,
				is CovLoopUntilStatement,
				is CovWhileStatement -> CovIcons.CONTROL_FLOW_ICON
				else -> CovIcons.COV_BIG_ICON
			}
		}

		override fun getAlphaSortKey() = presentableText
		override fun getPresentableText() = cutText(element.let { o ->
			when (o) {
				is CovFile -> "CovScript file"
				is CovFunctionDeclaration -> "function ${o.symbol.text}"
				is CovStructDeclaration -> "struct ${o.symbol.text}"
				is CovNamespaceDeclaration -> "namespace ${o.symbol.text}"
				is CovForStatement -> "for ${o.symbol.text} ${o.forIterate?.run { "iterate ${expression.text}" } ?: "to"}"
				is CovLoopUntilStatement -> "loop${o.expression?.run { " until $text" } ?: ""}"
				is CovWhileStatement -> "while ${o.expression.text}"
				is CovTryCatchStatement -> "try catch ${o.symbol.text}"
				is CovSwitchStatement -> "switch statement"
				is CovCollapsedStatement -> "collapsed block"
				is CovBlockStatement -> "begin block"
				is CovVariableDeclaration -> "var ${o.symbol.text}"
				is CovIfStatement -> "if ${o.expression.text}"
				else -> "??"
			}
		}, LONG_TEXT_MAX)

		override fun getLocationString() = ""
		override fun getLocationPrefix() = ""
		override fun getLocationSuffix() = ""
		override fun getChildrenBase(): List<CovStructureElement> = element.let { o ->
			@Suppress("UNCHECKED_CAST") when (o) {
				is CovFile -> o.children.mapNotNull { (it as? CovStatement)?.inside }
				is CovFunctionDeclaration -> o.bodyOfSomething.statementList.mapNotNull { it?.inside }
				is CovStructDeclaration -> o.functionDeclarationList + o.variableDeclarationList
				is CovNamespaceDeclaration -> o.bodyOfSomething.statementList.mapNotNull { it?.inside }
				is CovForStatement -> o.bodyOfSomething.statementList.mapNotNull { it?.inside }
				is CovLoopUntilStatement -> o.bodyOfSomething.statementList.mapNotNull { it?.inside }
				is CovWhileStatement -> o.bodyOfSomething.statementList.mapNotNull { it?.inside }
				is CovTryCatchStatement -> o.bodyOfSomethingList.flatMap { it.statementList.mapNotNull { it?.inside } }
				is CovSwitchStatement -> o.bodyOfSomethingList.flatMap { it.statementList.mapNotNull { it?.inside } }
				is CovBlockStatement -> o.bodyOfSomething.statementList.mapNotNull { it?.inside }
				is CovIfStatement -> o.bodyOfSomethingList.flatMap { it.statementList.mapNotNull { it?.inside } }
				is PsiElement -> o.children.mapNotNull { (it as? CovStatement)?.inside }
				else -> emptyList()
			}.map(::CovStructureElement)
		}
	}
}
