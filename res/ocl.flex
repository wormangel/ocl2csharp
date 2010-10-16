import java_cup.runtime.*;

%%

%class Lexer
%unicode
%cup
%line
%column

%{

  StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
  
  private int linha = 0;
  private boolean debug = false;
  
%}

delim	= [ \t\r]
ws		= {delim}+
letter	= [A-Za-z]
digit	= [0-9]
id		= [_]*{letter}({letter}|{digit})*
real	= {digit}+(\.{digit}+)?(E[+-]?{digit}+)?
integer = ([\-]({digit})* | ({digit})*)
string	= '~'
comment = [\-][\-]~[\r]


%%

// Pré e pós-condições

"pre"		{if(debug) System.out.print("( PRE )"); return symbol(sym.PRE); }
"pos"		{if(debug) System.out.print("( POS )"); return symbol(sym.POS); }

// Invariantes

"inv"		{if(debug) System.out.print("( INV )"); return symbol(sym.INV); }

// Expressões sobre coleções

"select"	{if(debug) System.out.print("( SELECT )"); return symbol(sym.SELECT); }
"exists"	{if(debug) System.out.print("( EXISTS )"); return symbol(sym.EXISTS); }
"forAll"	{if(debug) System.out.print("( FORALL )"); return symbol(sym.FORALL); }
"includes"	{if(debug) System.out.print("( INCLUDES )"); return symbol(sym.INCLUDES); }
"excludes"	{if(debug) System.out.print("( EXCLUDES )"); return symbol(sym.EXCLUDES); }
"size"		{if(debug) System.out.print("( SIZE )"); return symbol(sym.SIZE); }

// Expressões condicionais

"if"		{if(debug) System.out.print("( IF )"); return symbol(sym.IF); }
"then"		{if(debug) System.out.print("( THEN )"); return symbol(sym.THEN); }
"else"		{if(debug) System.out.print("( ELSE )"); return symbol(sym.ELSE); }
"endif"		{if(debug) System.out.print("( ENDIF )"); return symbol(sym.ENDIF); }

// Expressões lógicas, relacionais e aritméticas

"and"		{if(debug) System.out.print("( AND )"); return symbol(sym.AND, new Atributos(String.class, "and", "")); }
"or"		{if(debug) System.out.print("( OR )"); return symbol(sym.OR, new Atributos(String.class, "or", "")); }
"xor"		{if(debug) System.out.print("( XOR )"); return symbol(sym.XOR, new Atributos(String.class, "xor", "")); }
"not"		{if(debug) System.out.print("( NOT )"); return symbol(sym.NOT); }
"implies"	{if(debug) System.out.print("( IMPLIES )"); return symbol(sym.IMPLIES, new Atributos(String.class, "implies", "")); }

// Coleções

"set"		{if(debug) System.out.print("( SET )"); return symbol(sym.SET); }
"bag"		{if(debug) System.out.print("( BAG )"); return symbol(sym.BAG); }
"collection" {if(debug) System.out.print("( COLLECTION )"); return symbol(sym.COLLECTION); }
"sequence"	{if(debug) System.out.print("( SEQUENCE )"); return symbol(sym.SEQUENCE); }

// Mapeamento de classes, atributos e métodos

"package"		{if(debug) System.out.print("( PACKAGE )"); return symbol(sym.PACKAGE); }
"endpackage"		{if(debug) System.out.print("( ENDPACKAGE )"); return symbol(sym.ENDPACKAGE); }
"context"	{if(debug) System.out.print("( CONTEXT )"); return symbol(sym.CONTEXT); }

"integer"	{if(debug) System.out.print("( INTTYPE)"); return symbol(sym.INTTYPE); }
"real"		{if(debug) System.out.print("( REALTYPE )"); return symbol(sym.REALTYPE); }
"boolean"	{if(debug) System.out.print("( BOOLEAN )"); return symbol(sym.BOOLEAN); }
"true"		{if(debug) System.out.print("( TRUE )"); return symbol(sym.TRUE, new Atributos(Boolean.class, true, "")); }
"false"		{if(debug) System.out.print("( FALSE )"); return symbol(sym.FALSE, new Atributos(Boolean.class, false, "")); }
"void"		{if(debug) System.out.print("( VOID )"); return symbol(sym.VOID); }

// Sinais e operadores

"("			{if(debug) System.out.print("( LPAREN )"); return symbol(sym.LPAREN); }
")"			{if(debug) System.out.print("( RPAREN )"); return symbol(sym.RPAREN); }
"["			{if(debug) System.out.print("( LCOLCH )"); return symbol(sym.LCOLCH); }
"]"			{if(debug) System.out.print("( RCOLCH )"); return symbol(sym.RCOLCH); }
"{"			{if(debug) System.out.print("( LCHAVE )"); return symbol(sym.LCHAVE); }
"}"			{if(debug) System.out.print("( RCHAVE )"); return symbol(sym.RCHAVE); }	
":"			{if(debug) System.out.print("( 2PONTOS )"); return symbol(sym.DOISPONTOS); }
"::"		{if(debug) System.out.print("( 22PONTOS )"); return symbol(sym.QUATROPONTOS); }
","			{if(debug) System.out.print("( VIRGULA )"); return symbol(sym.VIRGULA); }
"="			{if(debug) System.out.print("( IGUAL )"); return symbol(sym.IGUAL); }
"<>"		{if(debug) System.out.print("( DIFF )"); return symbol(sym.DIFF); }
"<"			{if(debug) System.out.print("( MENORQ )"); return symbol(sym.MENORQ); }
">"			{if(debug) System.out.print("( MAIORQ )"); return symbol(sym.MAIORQ); }
"<="		{if(debug) System.out.print("( MENORIGUAL )"); return symbol(sym.MENORIGUAL); }
">="		{if(debug) System.out.print("( MAIORIGUAL )"); return symbol(sym.MAIORIGUAL); }
"->"		{if(debug) System.out.print("( SETA )"); return symbol(sym.SETA); }
".."		{if(debug) System.out.print("( PONTOPONTO )"); return symbol(sym.PONTOPONTO); }
"."			{if(debug) System.out.print("( PONTO )"); return symbol(sym.PONTO); }
"#"			{if(debug) System.out.print("( SUST )"); return symbol(sym.SHARP); }
";"			{if(debug) System.out.print("( PONTOVIRGULA )"); return symbol(sym.PONTOVIRGULA); }
"|"			{if(debug) System.out.print("( BARRA )"); return symbol(sym.BARRA); }
"+"			{if(debug) System.out.print("( MAIS )"); return symbol(sym.MAIS, "+"); }
"-"			{if(debug) System.out.print("( MENOS )"); return symbol(sym.MENOS, "-"); }
"*"			{if(debug) System.out.print("( VEZES )"); return symbol(sym.VEZES, "*"); }
"/"			{if(debug) System.out.print("( DIVIDIR )"); return symbol(sym.DIVIDIR, "/"); }

// Caracteres especiais

"\n"		{linha++;if(debug) System.out.println(); if(debug) System.out.println(linha+ "  ");}
"\R"		{linha++;if(debug) System.out.println(); if(debug) System.out.println(linha+ "  ");}
{ws}		{}

// Valores

{id}		{if(debug) System.out.print("( ID , "+ yytext() + " )" ); return symbol(sym.ID, new Atributos(String.class, yytext(),"")); }
{string}	{if(debug) System.out.print("( STR , "+yytext()+" )"  ); return symbol(sym.STRING, new Atributos(String.class, yytext(),"")); }
{real}		{if(debug) System.out.print("( REAL , " +yytext()+" )"); return symbol(sym.REAL, new Atributos(Double.class, yytext(),"")); }
{integer}	{if(debug) System.out.print("( INT , " +yytext()+" )"); return symbol(sym.INTEGER, new Atributos(Integer.class, yytext(),"")); }

{comment}	{}