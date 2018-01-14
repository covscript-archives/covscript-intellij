package org.covscript.lang

import com.intellij.openapi.util.IconLoader
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

@Nls const val COV_NAME = "CovScript"
@Nls const val COV_PKG_NAME = "CovScript Package"
@Nls const val COV_EXT_NAME = "CovScript Extension"
@NonNls const val COV_EXTENSION = "csc"
@NonNls const val COV_EXT_EXTENSION = "cse"
@NonNls const val COV_PKG_EXTENSION = "csp"

@Nls const val COV_SDK_NAME = "CovScript SDK"
@Nls const val COV_DESCRIPTION = "$COV_NAME Source File"
@Nls const val COV_PKG_DESCRIPTION = "$COV_NAME Package"
@Nls const val COV_EXT_DESCRIPTION = "$COV_NAME Extension File"
@Nls const val COV_MODULE_TYPE_DESCRIPTION = "$COV_NAME Module Type"

@Nls const val COV_RUN_CONFIG_DESCRIPTION = "$COV_NAME Run Configuration Type"

@NonNls const val COV_DEFAULT_MODULE_NAME = "my_bizarre_covscript_module"
@NonNls const val COV_RUN_CONFIG_ID = "COV_RUN_CONFIG_ID"
@NonNls const val POSSIBLE_SDK_HOME_LINUX = "/usr/share/covscript"
@NonNls const val POSSIBLE_SDK_HOME_WINDOWS = "C:/Program Files"
@NonNls const val COV_VERSION_COMMAND = """echo "runtime.info ()" | cs_repl --silent"""

@NonNls const val COV_WEBSITE = "http://covscript.org"

@JvmField val COV_ICON: Icon = IconLoader.getIcon("/icons/csc.png")
@JvmField val COV_PKG_ICON: Icon = IconLoader.getIcon("/icons/csp.png")
@JvmField val COV_EXT_ICON: Icon = IconLoader.getIcon("/icons/cse.png")
@JvmField val COV_BIG_ICON: Icon = IconLoader.getIcon("/icons/cov.png")

@JvmField val FUNCTION_ICON = IconLoader.getIcon("/icons/function.png")
@JvmField val NAMESPACE_ICON = IconLoader.getIcon("/icons/namespace.png")
@JvmField val CONTROL_FLOW_ICON = IconLoader.getIcon("/icons/control_flow.png")
@JvmField val JOJO_ICON: Icon = IconLoader.getIcon("/icons/jojo.png")

@JvmField val STRUCT_ICON = IconLoader.getIcon("/nodes/static.png")
@JvmField val COLLAPSED_ICON = IconLoader.getIcon("/nodes/annotationtype.png")
@JvmField val VARIABLE_ICON = IconLoader.getIcon("/nodes/variable.png")
@JvmField val TRY_CATCH_ICON = IconLoader.getIcon("/nodes/exceptionClass.png")
@JvmField val SWITCH_ICON = IconLoader.getIcon("/nodes/deploy.png")
@JvmField val BLOCK_ICON = IconLoader.getIcon("/nodes/anonymousClass.png")

@JvmField val COV_KEYWORDS = listOf(
		"if",
		"else",
		"end",
		"new",
		"gcnew",
		"typeid",
		"while",
		"for",
		"package",
		"using",
		"true",
		"false",
		"null",
		"import",
		"var",
		"const",
		"namespace",
		"function",
		"break",
		"continue",
		"return",
		"block",
		"to",
		"iterate",
		"until",
		"loop",
		"step",
		"throw",
		"try",
		"catch",
		"struct",
		"switch",
		"case",
		"default",
		"not",
		"and",
		"or")
