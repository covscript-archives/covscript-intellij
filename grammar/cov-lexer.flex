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

SYM=[a-zA-Z_][0-9a-zA-Z_]*
NUM=[0-9]+(\.[0-9]+)?

COLLAPSER_BEGIN=@begin
COLLAPSER_END=@end

EQ==
QUESTION_OP=\?
COLON_OP=:
DIV_ASS={DIV_OP}=
PLUS_ASS={PLUS_OP}=
MINUS_ASS={MINUS_OP}=
TIMES_ASS={TIMES_OP}=
POW_ASS={POW_OP}=
REM_ASS={REM_OP}=
QUESTION_OP=\?
PLUS_OP=\+
INC_OP={PLUS_OP}{PLUS_OP}
MINUS_OP=\-
DEC_OP={MINUS_OP}{MINUS_OP}
TIMES_OP=\*
DIV_OP=\/
REM_OP=%
POW_OP=\^
COLON_OP=:
AND_OP=&&
OR_OP=\|\|
UN_OP={NOT_OP}=
NOT_OP=\!

COMMA=,
ARROW=->
DOT=\.
LEFT_BRACKET=\(
RIGHT_BRACKET=\)
LEFT_B_BRACKET=\{
RIGHT_B_BRACKET=\}
LEFT_S_BRACKET=\[
RIGHT_S_BRACKET=\]

WHITE_SPACE=[ \t\r]
OTHERWISE=[^ \t\r]

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
for { return CovTypes.FOR_KEYWORD; }
package { return CovTypes.PACKAGE_KEYWORD; }
using { return CovTypes.USING_KEYWORD; }
true { return CovTypes.TRUE_KEYWORD; }
false { return CovTypes.FALSE_KEYWORD; }
null { return CovTypes.NULL_KEYWORD; }
import { return CovTypes.IMPORT_KEYWORD; }
var { return CovTypes.VAR_KEYWORD; }
const { return CovTypes.CONST_KEYWORD; }
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

{ARROW} { return CovTypes.ARROW; }
{COLON_OP} { return CovTypes.COLON_OP; }
{EQ} { return CovTypes.EQ; }
{DIV_ASS} { return CovTypes.DIV_ASS; }
{PLUS_ASS} { return CovTypes.PLUS_ASS; }
{MINUS_ASS} { return CovTypes.MINUS_ASS; }
{TIMES_ASS} { return CovTypes.TIMES_ASS; }
{POW_ASS} { return CovTypes.POW_ASS; }
{REM_ASS} { return CovTypes.REM_ASS; }
{QUESTION_OP} { return CovTypes.QUESTION_OP; }
{PLUS_OP} { return CovTypes.PLUS_OP; }
{MINUS_OP} { return CovTypes.MINUS_OP; }
{TIMES_OP} { return CovTypes.TIMES_OP; }
{DIV_OP} { return CovTypes.DIV_OP; }
{REM_OP} { return CovTypes.REM_OP; }
{POW_OP} { return CovTypes.POW_OP; }
{AND_OP} { return CovTypes.AND_OP; }
{OR_OP} { return CovTypes.OR_OP; }
'<' { return CovTypes.LT_OP; }
'>' { return CovTypes.GT_OP; }
'==' { return CovTypes.EQ_OP; }
'<=' { return CovTypes.LE_OP; }
'>=' { return CovTypes.GE_OP; }
{UN_OP} { return CovTypes.UN_OP; }
{INC_OP} { return CovTypes.INC_OP; }
{DEC_OP} { return CovTypes.DEC_OP; }
{NOT_OP} { return CovTypes.NOT_OP; }

{COMMA} { return CovTypes.COMMA; }
{DOT} { return CovTypes.DOT; }
{LEFT_BRACKET} { return CovTypes.LEFT_BRACKET; }
{RIGHT_BRACKET} { return CovTypes.RIGHT_BRACKET; }
{LEFT_B_BRACKET} { return CovTypes.LEFT_B_BRACKET; }
{RIGHT_B_BRACKET} { return CovTypes.RIGHT_B_BRACKET; }
{LEFT_S_BRACKET} { return CovTypes.LEFT_S_BRACKET; }
{RIGHT_S_BRACKET} { return CovTypes.RIGHT_S_BRACKET; }
{COMMENT} { return CovTypes.LINE_COMMENT; }
{EOL} { return CovTypes.EOL; }

{STRING_LITERAL} { return CovTypes.STR; }
{INCOMPLETE_STRING} { return TokenType.BAD_CHARACTER; }
{CHAR_LITERAL} { return CovTypes.CHAR; }
{INCOMPLETE_CHAR} { return TokenType.BAD_CHARACTER; }
{SYM} { return CovTypes.SYM; }
{NUM} { return CovTypes.NUM; }

{OTHERWISE} { return TokenType.BAD_CHARACTER; }
