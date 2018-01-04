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
COMMENT=#[^\n]*{EOL}
INCOMPLETE_STRING=\"([^\"\x00-\x1F\x7F\\]|\\[^])*
STRING_LITERAL={INCOMPLETE_STRING}\"
INCOMPLETE_CHAR='([^'\x00-\x1F\x7F\\]|\\[^])*
CHAR_LITERAL={INCOMPLETE_CHAR}'

SYM=[a-zA-Z_][0-9a-zA-Z_]*
NUM=[0-9]+(\.[0-9]+)?

IF_KEYWORD=if
ELSE_KEYWORD=else
END_KEYWORD=end
NEW_KEYWORD=new
GCNEW_KEYWORD=gcnew
TYPEID_KEYWORD=typeid
WHILE_KEYWORD=while
FOR_KEYWORD=for
PACKAGE_KEYWORD=package
USING_KEYWORD=using
TRUE_KEYWORD=true
FALSE_KEYWORD=false
NULL_KEYWORD=null
IMPORT_KEYWORD=import
VAR_KEYWORD=var
CONST_KEYWORD=const
NAMESPACE_KEYWORD=namespace
FUNCTION_KEYWORD=function
BREAK_KEYWORD=break
CONTINUE_KEYWORD=continue
RETURN_KEYWORD=return
BLOCK_KEYWORD=block
TO_KEYWORD=to
ITERATE_KEYWORD=iterate
UNTIL_KEYWORD=until
LOOP_KEYWORD=loop
STEP_KEYWORD=step
THROW_KEYWORD=throw
TRY_KEYWORD=try
CATCH_KEYWORD=catch
STRUCT_KEYWORD=struct
SWITCH_KEYWORD=switch
CASE_KEYWORD=case
DEFAULT_KEYWORD=default
AND_KEYWORD=and
OR_KEYWORD=or
NOT_KEYWORD=not

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
LT_OP=<
GT_OP=>
EQ_OP===
LE_OP=<=
GE_OP=>=
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

<COLLAPSING> {COLLAPSER_END} { yybegin(YYINITIAL); return CovTypes.COLLAPSER_END; }
<COLLAPSING> {EOL} {}
<YYINITIAL> {COLLAPSER_END} { return TokenType.BAD_CHARACTER; }

{COLLAPSER_BEGIN} { yybegin(COLLAPSING); return CovTypes.COLLAPSER_BEGIN; }

{NAMESPACE_KEYWORD} { return CovTypes.NAMESPACE_KEYWORD; }
{IF_KEYWORD} { return CovTypes.IF_KEYWORD; }
{ELSE_KEYWORD} { return CovTypes.ELSE_KEYWORD; }
{END_KEYWORD} { return CovTypes.END_KEYWORD; }
{NEW_KEYWORD} { return CovTypes.NEW_KEYWORD; }
{GCNEW_KEYWORD} { return CovTypes.GCNEW_KEYWORD; }
{TYPEID_KEYWORD} { return CovTypes.TYPEID_KEYWORD; }
{WHILE_KEYWORD} { return CovTypes.WHILE_KEYWORD; }
{FOR_KEYWORD} { return CovTypes.FOR_KEYWORD; }
{PACKAGE_KEYWORD} { return CovTypes.PACKAGE_KEYWORD; }
{USING_KEYWORD} { return CovTypes.USING_KEYWORD; }
{TRUE_KEYWORD} { return CovTypes.TRUE_KEYWORD; }
{FALSE_KEYWORD} { return CovTypes.FALSE_KEYWORD; }
{NULL_KEYWORD} { return CovTypes.NULL_KEYWORD; }
{IMPORT_KEYWORD} { return CovTypes.IMPORT_KEYWORD; }
{VAR_KEYWORD} { return CovTypes.VAR_KEYWORD; }
{CONST_KEYWORD} { return CovTypes.CONST_KEYWORD; }
{NAMESPACE_KEYWORD} { return CovTypes.NAMESPACE_KEYWORD; }
{FUNCTION_KEYWORD} { return CovTypes.FUNCTION_KEYWORD; }
{BREAK_KEYWORD} { return CovTypes.BREAK_KEYWORD; }
{CONTINUE_KEYWORD} { return CovTypes.CONTINUE_KEYWORD; }
{RETURN_KEYWORD} { return CovTypes.RETURN_KEYWORD; }
{BLOCK_KEYWORD} { return CovTypes.BLOCK_KEYWORD; }
{TO_KEYWORD} { return CovTypes.TO_KEYWORD; }
{ITERATE_KEYWORD} { return CovTypes.ITERATE_KEYWORD; }
{UNTIL_KEYWORD} { return CovTypes.UNTIL_KEYWORD; }
{LOOP_KEYWORD} { return CovTypes.LOOP_KEYWORD; }
{STEP_KEYWORD} { return CovTypes.STEP_KEYWORD; }
{THROW_KEYWORD} { return CovTypes.THROW_KEYWORD; }
{TRY_KEYWORD} { return CovTypes.TRY_KEYWORD; }
{CATCH_KEYWORD} { return CovTypes.CATCH_KEYWORD; }
{STRUCT_KEYWORD} { return CovTypes.STRUCT_KEYWORD; }
{SWITCH_KEYWORD} { return CovTypes.SWITCH_KEYWORD; }
{CASE_KEYWORD} { return CovTypes.CASE_KEYWORD; }
{DEFAULT_KEYWORD} { return CovTypes.DEFAULT_KEYWORD; }
{AND_KEYWORD} { return CovTypes.AND_KEYWORD; }
{OR_KEYWORD} { return CovTypes.OR_KEYWORD; }
{NOT_KEYWORD} { return CovTypes.NOT_KEYWORD; }

{ARROW} { return CovTypes.ARROW; }
{QUESTION_OP} { return CovTypes.QUESTION_OP; }
{COLON_OP} { return CovTypes.COLON_OP; }
{EQ} { return CovTypes.EQ; }
{QUESTION_OP} { return CovTypes.QUESTION_OP; }
{COLON_OP} { return CovTypes.COLON_OP; }
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
{COLON_OP} { return CovTypes.COLON_OP; }
{AND_OP} { return CovTypes.AND_OP; }
{OR_OP} { return CovTypes.OR_OP; }
{LT_OP} { return CovTypes.LT_OP; }
{GT_OP} { return CovTypes.GT_OP; }
{EQ_OP} { return CovTypes.EQ_OP; }
{LE_OP} { return CovTypes.LE_OP; }
{GE_OP} { return CovTypes.GE_OP; }
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

{STRING_LITERAL} {  return CovTypes.STR; }
{INCOMPLETE_STRING} { return TokenType.BAD_CHARACTER; }
{CHAR_LITERAL} {  return CovTypes.CHAR; }
{INCOMPLETE_CHAR} { return TokenType.BAD_CHARACTER; }
{SYM} { return CovTypes.SYM; }
{NUM} { return CovTypes.NUM; }

{WHITE_SPACE}+ { return TokenType.WHITE_SPACE; }
{OTHERWISE} { return TokenType.BAD_CHARACTER; }
