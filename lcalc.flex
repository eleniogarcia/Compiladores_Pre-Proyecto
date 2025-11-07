/* ==========================================================
   File: mini.flex
   Lexer para el lenguaje Mini
   ========================================================== */

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

/* ------------ Macros ------------ */
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
dec_int_lit    = 0 | [1-9][0-9]*
dec_id         = [A-Za-z_][A-Za-z_0-9]*

%%

<YYINITIAL> {

    /* -------- Palabras reservadas -------- */
    "void"      { return symbol(sym.VOID); }
    "int"       { return symbol(sym.INT); }
    "if"        { return symbol(sym.IF); }
    "then"      { return symbol(sym.THEN); }
    "else"      { return symbol(sym.ELSE); }
    "while"     { return symbol(sym.WHILE); }
    "return"    { return symbol(sym.RETURN); }

    /* -------- Operadores aritméticos -------- */
    "+"         { return symbol(sym.PLUS); }
    "-"         { return symbol(sym.MINUS); }
    "*"         { return symbol(sym.TIMES); }
    "/"         { return symbol(sym.DIVIDE); }

    /* -------- Operadores lógicos -------- */
    "&&"        { return symbol(sym.AND); }
    "||"        { return symbol(sym.OR); }
    "!"         { return symbol(sym.NOT); }

    /* -------- Operadores relacionales -------- */
    "=="        { return symbol(sym.EQEQ); }
    "<"         { return symbol(sym.LT); }
    ">"         { return symbol(sym.GT); }

    /* -------- Delimitadores -------- */
    "="         { return symbol(sym.ASSIGN); }
    ";"         { return symbol(sym.SEMI); }
    "("         { return symbol(sym.LPAREN); }
    ")"         { return symbol(sym.RPAREN); }
    "{"         { return symbol(sym.LBRACE); }
    "}"         { return symbol(sym.RBRACE); }
    ","         { return symbol(sym.COMMA); }

    /* -------- Constantes numéricas -------- */
    {dec_int_lit} {
        return symbol(sym.NUMBER, Integer.valueOf(yytext()));
    }

    /* -------- Identificadores -------- */
    {dec_id} {
        return symbol(sym.ID, yytext());
    }

    /* -------- Espacios en blanco -------- */
    {WhiteSpace} { /* ignorar */ }
}

/* -------- Error léxico -------- */
[^] { throw new Error("Caracter ilegal: <" + yytext() + "> en línea " + (yyline+1)); }
