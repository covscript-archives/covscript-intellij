package org.covscript.lang

import java.util.regex.Pattern.compile

val pattern = compile(""""([^"\x00-\x1F\x7F\\]|\\[\\'"abfnrtv0])*"""")
