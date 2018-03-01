package org.covscript.lang

import org.jetbrains.annotations.NonNls

@NonNls const val COV_EXTENSION = "csc"
@NonNls const val COV_EXT_EXTENSION = "cse"
@NonNls const val COV_PKG_EXTENSION = "csp"

@NonNls const val COV_DEFAULT_MODULE_NAME = "my_bizarre_covscript_module"
@NonNls const val COV_RUN_CONFIG_ID = "COV_RUN_CONFIG_ID"
@NonNls const val COV_SDK_HOME_ID = "COV_SDK_HOME_ID"
@NonNls const val COV_MODULE_ID = "COV_MODULE_TYPE"
@NonNls const val POSSIBLE_EXE_LINUX = "/usr/bin/cs"
@NonNls const val POSSIBLE_EXE_WINDOWS = "C:/Program Files"

@NonNls const val COV_WEBSITE = "http://covscript.org"

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
