package hypertalk.parser;

import java_cup.runtime.SymbolFactory;
import java_cup.runtime.ComplexSymbolFactory;

%%

%class HtLexer
%public 
%{
   private SymbolFactory sf = new ComplexSymbolFactory ();
   public HtLexer (java.io.InputStream r, SymbolFactory sf)
   {
     this (r);
     this.sf = sf;
   }
%}
%eofval{
  return sf.newSymbol ("EOF", sym.EOF);
%eofval}

%unicode

%cup
%cupdebug

%char
%column
%line


ALPHA=[A-Za-z_]
DIGIT=[0-9]
NONNEWLINE_WHITE_SPACE_CHAR=[\ \t\b]
NEWLINE=\r|\n|\r\n
WHITE_SPACE_CHAR=[\n\r\ \t\b\012]
CHAR_TEXT=\\\"|[^\n\r\"]
STRING_TEXT=(\\\"|[^\n\r\"]|\\{WHITE_SPACE_CHAR}+\\)*
IDENT={ALPHA}({ALPHA}|{DIGIT}|_)*

%% 

<YYINITIAL> {
				
	"answer" {return sf.newSymbol ("answer", sym.ANSWER); }
	"ask" {return sf.newSymbol ("ask", sym.ASK); }
	"with" {return sf.newSymbol ("with", sym.WITH); }
	"and" {return sf.newSymbol ("and", sym.AND); }		
	"or" {return sf.newSymbol ("or", sym.OR); }		
	"contains" {return sf.newSymbol ("contains", sym.CONTAINS); }
	"is" {return sf.newSymbol ("is", sym.IS); }
	"is in" {return sf.newSymbol ("is in", sym.ISIN); }	
	"is not in" {return sf.newSymbol ("is not in", sym.ISNOTIN); }
	"is not" {return sf.newSymbol ("is not", sym.ISNOT); }
	"put" {return sf.newSymbol ("put", sym.PUT); }
	"get" {return sf.newSymbol ("get", sym.GET); }
	"before" {return sf.newSymbol ("before", sym.BEFORE); }
	"after" {return sf.newSymbol ("after", sym.AFTER); }
	"into" {return sf.newSymbol ("into", sym.INTO); }
	"not" {return sf.newSymbol ("not", sym.NOT); }
	"word" {return sf.newSymbol ("word", sym.WORD); }
	"item" {return sf.newSymbol ("item", sym.ITEM); }
	"line" {return sf.newSymbol ("line", sym.LINE); }
	"char" {return sf.newSymbol ("char", sym.CHAR); }
	"character" {return sf.newSymbol ("character", sym.CHAR); }
	"of" {return sf.newSymbol ("of", sym.OF); }
	"to" {return sf.newSymbol ("to", sym.TO); }
	"the" {return sf.newSymbol ("the", sym.THE); }
	"global" {return sf.newSymbol ("global", sym.GLOBAL); }		
	"function" {return sf.newSymbol ("function", sym.FUNCTION); }
	"on" {return sf.newSymbol ("on", sym.ON); }
	"end" {return sf.newSymbol ("end", sym.END); }
	"if" {return sf.newSymbol ("if", sym.IF); }
	"then" {return sf.newSymbol ("then", sym.THEN); }
	"end if" {return sf.newSymbol ("end if", sym.ENDIF); }
	"else" {return sf.newSymbol ("else", sym.ELSE); }
	"repeat" {return sf.newSymbol ("repeat", sym.REPEAT); }
	"end repeat" {return sf.newSymbol ("end repeat", sym.ENDREPEAT); }
	"forever" {return sf.newSymbol ("forever", sym.FOREVER); }
	"until" {return sf.newSymbol ("until", sym.UNTIL); }
	"while" {return sf.newSymbol ("while", sym.WHILE); }
	"for" {return sf.newSymbol ("for", sym.FOR); }
	"times" {return sf.newSymbol ("times", sym.TIMES); }
	"down to" {return sf.newSymbol ("down to", sym.DOWNTO); }
	"do" {return sf.newSymbol ("do", sym.DO); }
	"field" {return sf.newSymbol ("do", sym.FIELD); }
	"button" {return sf.newSymbol ("button", sym.BUTTON); }
	"id" {return sf.newSymbol ("id", sym.ID); }
	"message window" {return sf.newSymbol ("id", sym.MESSAGEBOX); }
	"message box" {return sf.newSymbol ("id", sym.MESSAGEBOX); }
	"message" {return sf.newSymbol ("id", sym.MESSAGEBOX); }
	"set" {return sf.newSymbol ("set", sym.SET); }
	"send" {return sf.newSymbol ("send", sym.SEND); }
	"me" {return sf.newSymbol ("me", sym.ME); }
	"return" {return sf.newSymbol ("return", sym.RETURN_KEYWORD); }

	"mouseLoc" {return sf.newSymbol ("mouseLoc", sym.MOUSELOC); }
	"mouse" {return sf.newSymbol ("mouse", sym.MOUSE); }
	"average" {return sf.newSymbol ("average", sym.AVERAGE); }
	"result" {return sf.newSymbol ("result", sym.RESULT); }
    "min" {return sf.newSymbol ("min", sym.MIN); }
    "max" {return sf.newSymbol ("max", sym.MAX); }

	"number" {return sf.newSymbol ("number", sym.NUMBER); }
	"in" {return sf.newSymbol ("in", sym.IN); }
	"chars" {return sf.newSymbol ("chars", sym.CHARS); }
	"characters" {return sf.newSymbol ("chars", sym.CHARS); }
	"lines" {return sf.newSymbol ("lines", sym.LINES); }
	"items" {return sf.newSymbol ("items", sym.ITEMS); }
	"words" {return sf.newSymbol ("words", sym.WORDS); }
	
	"first" {return sf.newSymbol ("first", sym.FIRST); }
	"second" {return sf.newSymbol ("second", sym.SECOND); }
	"third" {return sf.newSymbol ("third", sym.THIRD); }
	"fourth" {return sf.newSymbol ("fourth", sym.FOURTH); }
	"fifth" {return sf.newSymbol ("fifth", sym.FIFTH); }
	"sixth" {return sf.newSymbol ("sixth", sym.SIXTH); }
	"seventh" {return sf.newSymbol ("seventh", sym.SEVENTH); }
	"eighth" {return sf.newSymbol ("eighth", sym.EIGTH); }
	"ninth" {return sf.newSymbol ("ninth", sym.NINTH); }
	"tenth" {return sf.newSymbol ("tenth", sym.TENTH); }
	"mid" {return sf.newSymbol ("mid", sym.MID); }
	"middle" {return sf.newSymbol ("middle", sym.MIDDLE); }
	"last" {return sf.newSymbol ("last", sym.LAST); }
	
	"-" {return sf.newSymbol ("-", sym.MINUS); }
	"+" {return sf.newSymbol ("+", sym.PLUS); }
	"*" {return sf.newSymbol ("*", sym.STAR); }
	"/" {return sf.newSymbol ("/", sym.SLASH); }
	"=" {return sf.newSymbol ("=", sym.EQUALS); }
	"<" {return sf.newSymbol ("<", sym.LESS); }
	">" {return sf.newSymbol (">", sym.GREATER); }
	"<>" {return sf.newSymbol ("<>", sym.LESSGREATER); }
	"<=" {return sf.newSymbol ("<=", sym.LESSEQUALS); }
	">=" {return sf.newSymbol (">=", sym.GREATEREQUALS); }
	"&&" {return sf.newSymbol ("&&", sym.AMPAMP); }
	"&" {return sf.newSymbol ("&", sym.AMP); }
	"div" {return sf.newSymbol ("div", sym.DIV); }
	"mod" {return sf.newSymbol ("mod", sym.MOD); }
	"^" {return sf.newSymbol ("^", sym.CARET); } 
	
	"(" {return sf.newSymbol ("(", sym.LPAREN); }
	")" {return sf.newSymbol (")", sym.RPAREN); }
	"." {return sf.newSymbol (".", sym.DOT); }
	"," {return sf.newSymbol (",", sym.COMMA); }

	{IDENT} {
	    return sf.newSymbol ("Identifier", sym.IDENTIFIER, yytext ());
	}
	
	\" {STRING_TEXT} \" {
	  	String str = yytext ().substring (1, yylength () - 1);
	    return sf.newSymbol ("literal", sym.LITERAL, str); 
	}
	
	{DIGIT}+ {
		int i = Integer.parseInt (yytext ());
		return sf.newSymbol ("IntegerConstant", sym.INTEGER_CONSTANT, new Integer (i));
	}
  	
	"--" .* {NEWLINE}+ { return sf.newSymbol ("Return", sym.RETURN); }	
	{NEWLINE}+ { return sf.newSymbol ("Return", sym.RETURN); }	
	{NONNEWLINE_WHITE_SPACE_CHAR}+ {}
  	  	
	. {
		System.out.println ("Illegal character: <" + yytext () + ">");
	}

}

