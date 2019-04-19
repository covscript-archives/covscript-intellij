package org.covscript.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import org.covscript.lang.psi.CovTypes;

%%

%{
  public CovLexer() { this((java.io.Reader) null); }
%}

%class CovLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{ return;
%eof}

EOL=\n
COMMENT=#[^\n]*
INCOMPLETE_STRING=\"([^\"\\\n]|\\[^])*
STRING_LITERAL={INCOMPLETE_STRING}\"
INCOMPLETE_COLLAPSING_STRING=\"([^\"\\]|\\[^])*
COLLAPSING_STRING_LITERAL={INCOMPLETE_COLLAPSING_STRING}\"
INCOMPLETE_CHAR='([^'\\\n]|\\[^])*
CHAR_LITERAL={INCOMPLETE_CHAR}'

SYM=[a-zA-Z_][\da-zA-Z_]*
NUM=\d+(\.\d+)?

COLLAPSER_BEGIN=@begin
COLLAPSER_END=@end

WHITE_SPACE=[ \t\r]
OTHERWISE=[^]

%state COLLAPSING

%%

{WHITE_SPACE}+ { return TokenType.WHITE_SPACE; }

<COLLAPSING> {COLLAPSER_END} { yybegin(YYINITIAL); return CovTypes.COLLAPSER_END; }
<COLLAPSING> {INCOMPLETE_COLLAPSING_STRING} { return TokenType.BAD_CHARACTER; }
<COLLAPSING> {COLLAPSING_STRING_LITERAL} { return CovTypes.STR; }
<COLLAPSING> {EOL} { return TokenType.WHITE_SPACE; }
<YYINITIAL> {COLLAPSER_END} { return TokenType.BAD_CHARACTER; }

<YYINITIAL> {COLLAPSER_BEGIN} { yybegin(COLLAPSING); return CovTypes.COLLAPSER_BEGIN; }
<COLLAPSING> {COLLAPSER_BEGIN} { return TokenType.BAD_CHARACTER; }

namespace { return CovTypes.NAMESPACE_KEYWORD; }
if { return CovTypes.IF_KEYWORD; }
else { return CovTypes.ELSE_KEYWORD; }
end { return CovTypes.END_KEYWORD; }
new { return CovTypes.NEW_KEYWORD; }
gcnew { return CovTypes.GCNEW_KEYWORD; }
typeid { return CovTypes.TYPEID_KEYWORD; }
while { return CovTypes.WHILE_KEYWORD; }
foreach { return CovTypes.FOREACH_KEYWORD; }
class { return CovTypes.CLASS_KEYWORD; }
for { return CovTypes.FOR_KEYWORD; }
package { return CovTypes.PACKAGE_KEYWORD; }
using { return CovTypes.USING_KEYWORD; }
true { return CovTypes.TRUE_KEYWORD; }
false { return CovTypes.FALSE_KEYWORD; }
null { return CovTypes.NULL_KEYWORD; }
import { return CovTypes.IMPORT_KEYWORD; }
var { return CovTypes.VAR_KEYWORD; }
const { return CovTypes.CONST_KEYWORD; }
constant { return CovTypes.CONSTANT_KEYWORD; }
do { return CovTypes.DO_KEYWORD; }
namespace { return CovTypes.NAMESPACE_KEYWORD; }
function { return CovTypes.FUNCTION_KEYWORD; }
break { return CovTypes.BREAK_KEYWORD; }
continue { return CovTypes.CONTINUE_KEYWORD; }
return { return CovTypes.RETURN_KEYWORD; }
block { return CovTypes.BLOCK_KEYWORD; }
to { return CovTypes.TO_KEYWORD; }
iterate { return CovTypes.ITERATE_KEYWORD; }
until { return CovTypes.UNTIL_KEYWORD; }
loop { return CovTypes.LOOP_KEYWORD; }
step { return CovTypes.STEP_KEYWORD; }
throw { return CovTypes.THROW_KEYWORD; }
try { return CovTypes.TRY_KEYWORD; }
catch { return CovTypes.CATCH_KEYWORD; }
struct { return CovTypes.STRUCT_KEYWORD; }
switch { return CovTypes.SWITCH_KEYWORD; }
case { return CovTypes.CASE_KEYWORD; }
default { return CovTypes.DEFAULT_KEYWORD; }
and { return CovTypes.AND_KEYWORD; }
or { return CovTypes.OR_KEYWORD; }
not { return CovTypes.NOT_KEYWORD; }
extends { return CovTypes.EXTENDS_KEYWORD; }
override { return CovTypes.OVERRIDE_KEYWORD; }

-\> { return CovTypes.ARROW; }
\: { return CovTypes.COLON_SYM; }
= { return CovTypes.EQ; }
\/= { return CovTypes.DIV_ASS; }
\+= { return CovTypes.PLUS_ASS; }
-= { return CovTypes.MINUS_ASS; }
\*= { return CovTypes.TIMES_ASS; }
\^= { return CovTypes.POW_ASS; }
%= { return CovTypes.REM_ASS; }
\? { return CovTypes.QUESTION_SYM; }
\+ { return CovTypes.PLUS_SYM; }
- { return CovTypes.MINUS_SYM; }
\* { return CovTypes.TIMES_SYM; }
\/ { return CovTypes.DIV_SYM; }
% { return CovTypes.REM_SYM; }
\^ { return CovTypes.POW_SYM; }
&& { return CovTypes.AND_SYM; }
\|\| { return CovTypes.OR_SYM; }
\< { return CovTypes.LT_SYM; }
\> { return CovTypes.GT_SYM; }
== { return CovTypes.EQ_SYM; }
\<= { return CovTypes.LE_SYM; }
\>= { return CovTypes.GE_SYM; }
\!= { return CovTypes.UN_SYM; }
\+\+ { return CovTypes.INC_SYM; }
-- { return CovTypes.DEC_SYM; }
\! { return CovTypes.NOT_SYM; }

, { return CovTypes.COMMA; }
"..." { return CovTypes.TRIPLE_DOT; }
\. { return CovTypes.DOT; }
\( { return CovTypes.LEFT_BRACKET; }
\) { return CovTypes.RIGHT_BRACKET; }
\{ { return CovTypes.LEFT_B_BRACKET; }
\} { return CovTypes.RIGHT_B_BRACKET; }
\[ { return CovTypes.LEFT_S_BRACKET; }
\] { return CovTypes.RIGHT_S_BRACKET; }
{COMMENT} { return CovTokenType.LINE_COMMENT; }
{EOL} { return CovTypes.EOL; }

{STRING_LITERAL} { return CovTypes.STR; }
{INCOMPLETE_STRING} { return TokenType.BAD_CHARACTER; }
{CHAR_LITERAL} { return CovTypes.CHAR; }
{INCOMPLETE_CHAR} { return TokenType.BAD_CHARACTER; }
{SYM} { return CovTypes.SYM; }
{NUM} { return CovTypes.NUM; }

{OTHERWISE} { return TokenType.BAD_CHARACTER; }
