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

EOF=\n
COMMENT=#[^\n]*{EOF}

INCOMPLETE_STRING=\"[^\"]*
STRING_LITERAL={INCOMPLETE_STRING}\"

SYM=[a-zA-Z_][0-9a-zA-Z_]*
NUM=[0-9]+(\.[0-9]+)?

IF_KEYWORD=if
ELSE_KEYWORD=else
END_KEYWORD=end
NEW_KEYWORD=new
GCNEW_KEYWORD=gcnew
WHILE_KEYWORD=while
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
MINUS_OP=\-
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
UN_OP=\!=

COMMA=,
DOT=\.
LEFT_BRACKET=\(
RIGHT_BRACKET=\)
LEFT_B_BRACKET=\{
RIGHT_B_BRACKET=\}
LEFT_S_BRACKET=\[
RIGHT_S_BRACKET=\]

WHITE_SPACE=[ \t\r]
NON_WHITE_SPACE=[^ \t\r]

%%

{NAMESPACE_KEYWORD} { yybegin(YYINITIAL); return CovTypes.NAMESPACE_KEYWORD; }
{IF_KEYWORD} { yybegin(YYINITIAL); return CovTypes.IF_KEYWORD; }
{ELSE_KEYWORD} { yybegin(YYINITIAL); return CovTypes.ELSE_KEYWORD; }
{END_KEYWORD} { yybegin(YYINITIAL); return CovTypes.END_KEYWORD; }
{NEW_KEYWORD} { yybegin(YYINITIAL); return CovTypes.NEW_KEYWORD; }
{GCNEW_KEYWORD} { yybegin(YYINITIAL); return CovTypes.GCNEW_KEYWORD; }
{WHILE_KEYWORD} { yybegin(YYINITIAL); return CovTypes.WHILE_KEYWORD; }
{PACKAGE_KEYWORD} { yybegin(YYINITIAL); return CovTypes.PACKAGE_KEYWORD; }
{USING_KEYWORD} { yybegin(YYINITIAL); return CovTypes.USING_KEYWORD; }
{TRUE_KEYWORD} { yybegin(YYINITIAL); return CovTypes.TRUE_KEYWORD; }
{FALSE_KEYWORD} { yybegin(YYINITIAL); return CovTypes.FALSE_KEYWORD; }
{NULL_KEYWORD} { yybegin(YYINITIAL); return CovTypes.NULL_KEYWORD; }
{IMPORT_KEYWORD} { yybegin(YYINITIAL); return CovTypes.IMPORT_KEYWORD; }
{VAR_KEYWORD} { yybegin(YYINITIAL); return CovTypes.VAR_KEYWORD; }
{CONST_KEYWORD} { yybegin(YYINITIAL); return CovTypes.CONST_KEYWORD; }
{NAMESPACE_KEYWORD} { yybegin(YYINITIAL); return CovTypes.NAMESPACE_KEYWORD; }
{FUNCTION_KEYWORD} { yybegin(YYINITIAL); return CovTypes.FUNCTION_KEYWORD; }
{BREAK_KEYWORD} { yybegin(YYINITIAL); return CovTypes.BREAK_KEYWORD; }
{CONTINUE_KEYWORD} { yybegin(YYINITIAL); return CovTypes.CONTINUE_KEYWORD; }
{BLOCK_KEYWORD} { yybegin(YYINITIAL); return CovTypes.BLOCK_KEYWORD; }
{TO_KEYWORD} { yybegin(YYINITIAL); return CovTypes.TO_KEYWORD; }
{ITERATE_KEYWORD} { yybegin(YYINITIAL); return CovTypes.ITERATE_KEYWORD; }
{UNTIL_KEYWORD} { yybegin(YYINITIAL); return CovTypes.UNTIL_KEYWORD; }
{LOOP_KEYWORD} { yybegin(YYINITIAL); return CovTypes.LOOP_KEYWORD; }
{STEP_KEYWORD} { yybegin(YYINITIAL); return CovTypes.STEP_KEYWORD; }
{THROW_KEYWORD} { yybegin(YYINITIAL); return CovTypes.THROW_KEYWORD; }
{TRY_KEYWORD} { yybegin(YYINITIAL); return CovTypes.TRY_KEYWORD; }
{CATCH_KEYWORD} { yybegin(YYINITIAL); return CovTypes.CATCH_KEYWORD; }
{STRUCT_KEYWORD} { yybegin(YYINITIAL); return CovTypes.STRUCT_KEYWORD; }
{SWITCH_KEYWORD} { yybegin(YYINITIAL); return CovTypes.SWITCH_KEYWORD; }
{CASE_KEYWORD} { yybegin(YYINITIAL); return CovTypes.CASE_KEYWORD; }
{DEFAULT_KEYWORD} { yybegin(YYINITIAL); return CovTypes.DEFAULT_KEYWORD; }
{AND_KEYWORD} { yybegin(YYINITIAL); return CovTypes.AND_KEYWORD; }
{OR_KEYWORD} { yybegin(YYINITIAL); return CovTypes.OR_KEYWORD; }

{COMMA} { yybegin(YYINITIAL); return CovTypes.COMMA; }
{DOT} { yybegin(YYINITIAL); return CovTypes.DOT; }
{QUESTION_OP} { yybegin(YYINITIAL); return CovTypes.QUESTION_OP; }
{COLON_OP} { yybegin(YYINITIAL); return CovTypes.COLON_OP; }
{EQ} { yybegin(YYINITIAL); return CovTypes.EQ; }
{LEFT_BRACKET} { yybegin(YYINITIAL); return CovTypes.LEFT_BRACKET; }
{RIGHT_BRACKET} { yybegin(YYINITIAL); return CovTypes.RIGHT_BRACKET; }
{LEFT_B_BRACKET} { yybegin(YYINITIAL); return CovTypes.LEFT_B_BRACKET; }
{RIGHT_B_BRACKET} { yybegin(YYINITIAL); return CovTypes.RIGHT_B_BRACKET; }
{LEFT_S_BRACKET} { yybegin(YYINITIAL); return CovTypes.LEFT_S_BRACKET; }
{RIGHT_S_BRACKET} { yybegin(YYINITIAL); return CovTypes.RIGHT_S_BRACKET; }
{COMMENT} { yybegin(YYINITIAL); return CovTypes.LINE_COMMENT; }
{EOF} { yybegin(YYINITIAL); return CovTypes.EOF; }

{STRING_LITERAL} { yybegin(YYINITIAL);  return CovTypes.STR; }
{INCOMPLETE_STRING} { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
{SYM} { yybegin(YYINITIAL); return CovTypes.SYM; }
{NUM} { yybegin(YYINITIAL); return CovTypes.NUM; }

{WHITE_SPACE}+ { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }
{NON_WHITE_SPACE} { return TokenType.BAD_CHARACTER; }
