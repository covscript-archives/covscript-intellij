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
COMMA=,
DOT=\.
QUESTION_SIGN=\?
COLON=:
EQ==
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
{COMMA} { yybegin(YYINITIAL); return CovTypes.COMMA; }
{DOT} { yybegin(YYINITIAL); return CovTypes.DOT; }
{QUESTION_SIGN} { yybegin(YYINITIAL); return CovTypes.QUESTION_SIGN; }
{COLON} { yybegin(YYINITIAL); return CovTypes.COLON; }
{EQ} { yybegin(YYINITIAL); return CovTypes.EQ; }
{LEFT_BRACKET} { yybegin(YYINITIAL); return CovTypes.LEFT_BRACKET; }
{RIGHT_BRACKET} { yybegin(YYINITIAL); return CovTypes.RIGHT_BRACKET; }
{LEFT_B_BRACKET} { yybegin(YYINITIAL); return CovTypes.LEFT_B_BRACKET; }
{RIGHT_B_BRACKET} { yybegin(YYINITIAL); return CovTypes.RIGHT_B_BRACKET; }
{LEFT_S_BRACKET} { yybegin(YYINITIAL); return CovTypes.LEFT_S_BRACKET; }
{RIGHT_S_BRACKET} { yybegin(YYINITIAL); return CovTypes.RIGHT_S_BRACKET; }
{COMMENT} { yybegin(YYINITIAL); return CovTypes.LINE_COMMENT; }
{INCOMPLETE_STRING} { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
{STRING_LITERAL} { yybegin(YYINITIAL);  return CovTypes.STR; }
{EOF} { yybegin(YYINITIAL); return CovTypes.EOF; }
{SYM} { yybegin(YYINITIAL); return CovTypes.SYM; }

{WHITE_SPACE}+ { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }
{NON_WHITE_SPACE} { return TokenType.BAD_CHARACTER; }
