/*
  File Name: lcalc.flex
*/

import java_cup.runtime.*;

%%

%class Lexer
%line
%column
%cup

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

/* Macros */
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
dec_int_lit    = 0 | [1-9][0-9]*
dec_int_id     = [A-Za-z_][A-Za-z_0-9]*

%%

<YYINITIAL> {
    /* Palabras reservadas */
    "void"     { System.out.print(" void "); return symbol(sym.VOID); }
    "main"     { System.out.print(" main "); return symbol(sym.MAIN); }
    "int"      { System.out.print(" int ");  return symbol(sym.INT); }

    /* Operadores y delimitadores */
    "="        { System.out.print(" = "); return symbol(sym.ASSIGN); }
    ";"        { System.out.print(" ; "); return symbol(sym.SEMI); }
    "+"        { System.out.print(" + "); return symbol(sym.PLUS); }
    "-"        { System.out.print(" - "); return symbol(sym.MINUS); }
    "*"        { System.out.print(" * "); return symbol(sym.TIMES); }
    "/"        { System.out.print(" / "); return symbol(sym.DIVIDE); }
    "("        { System.out.print(" ( "); return symbol(sym.LPAREN); }
    ")"        { System.out.print(" ) "); return symbol(sym.RPAREN); }
    "{"        { System.out.print(" { "); return symbol(sym.LBRACE); }
    "}"        { System.out.print(" } "); return symbol(sym.RBRACE); }

    /* Constantes enteras */
    {dec_int_lit} {
        System.out.print(yytext());
        return symbol(sym.NUMBER, Integer.valueOf(yytext()));
    }

    /* Identificadores */
    {dec_int_id} {
        System.out.print(yytext());
        return symbol(sym.ID, yytext());
    }

    /* Espacios en blanco */
    {WhiteSpace} { /* skip */ }
}

/* Cualquier otro caracter es ilegal */
[^] { throw new Error("Illegal character <"+yytext()+">"); }
