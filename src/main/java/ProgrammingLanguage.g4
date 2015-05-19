grammar ProgrammingLanguage;

parse
    : functionDefinition* EOF
    ;

functionDefinition
	: typeSpecifier Id '(' idList? ')' functionBody
	;
    
idList
	: typeSpecifier Id (',' typeSpecifier Id)*
	;
    
typeSpecifier
	: Void_type
	| Int_type
	| Bool_type
	| String_type
	;
    
functionBody
    : '{' statement* (Return expression ';')? '}'
    ;
    
compoundStatement
	: '{' statement* '}'
	;
	
statement
	: compoundStatement
	| selectionStatement
	| whileStatement
	| jumpStatement
	| assignment ';'
    | functionCall ';'
    | declarationId ';'
    | unionStatement
	;
    
unionStatement
    : Union '{' (declarationId ';')+ '}'
    ;
	
selectionStatement
	: If '(' expression ')' compoundStatement
	| If '(' expression ')' compoundStatement Else compoundStatement
	| Switch '(' expression ')' '{' caseStatement* '}'
	;
    
caseStatement
    : Case expression ':' compoundStatement
    ;
	
jumpStatement
	: Break ';'
	| Continue ';'
	;
	
whileStatement
	: While '(' expression ')' compoundStatement
	;
    
functionCall
    : Id '(' expressionList? ')'
    | Write '(' expression ')'
    | Read '(' Id ')'
    | Length '(' expression ')'
    ;

expressionList
    : expression (',' expression)*  
    ;
	
expression
	: andExpr ('||' andExpr)*
	;
	
andExpr
	: eqExpr ('&&' eqExpr)*
	;
	
eqExpr  
  :  relExpr (('==' | '!=') relExpr)*  
  ;  
	
relExpr  
  :  addExpr (('>=' | '<=' | '>' | '<') addExpr)*  
  ;  
	
addExpr  
  :  mulExpr (('+' | '-') mulExpr)*  
  ;  
  
mulExpr  
  :  atom (('*' | '/' | '%') atom)*
  ;  
  
atom  
  :  Int
  |  Bool  
  |  lookup  
  ;  
  
lookup  
  :  functionCall index?
  |  '(' expression ')' index? 
  |  Id index?  
  |  String index?  
  ; 
  
index
  :  '[' expression ']' 
  ;
	
assignment
	: Id '=' expression
	;
	
declarationId
	: typeSpecifier Id (',' Id)*
	;
    
/**------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------
 */
While	: 'while' ;
Break	: 'break' ;
Continue	: 'continue' ;
Switch	: 'switch' ;
Case    : 'case' ;
Return  : 'return' ;
If	: 'if' ;
Else	: 'else' ;
Union : 'union' ;
Write : 'write' ;
Read  : 'read' ;
Length : 'length';

Void_type : 'void';
Int_type : 'int';
Bool_type : 'bool';
String_type : 'string';

Or       : '||';  
And      : '&&';  
Equals   : '==';  
NEquals  : '!=';  
GTEquals : '>=';  
LTEquals : '<=';  
Pow      : '^';  
Excl     : '!';  
GT       : '>';  
LT       : '<';  
Add      : '+';  
Subtract : '-';  
Multiply : '*';  
Divide   : '/';  
Modulus  : '%';  
OBrace   : '{';  
CBrace   : '}';  
OBracket : '[';  
CBracket : ']';  
OParen   : '(';  
CParen   : ')';  
SColon   : ';';  
Assign   : '=';  
Comma    : ',';  
QMark    : '?';  
Colon    : ':';  

Int : [0-9]+;
Bool : 'false' | 'true' ;
String : ["] (~["\r\n] | '\\\\' | '\\"')* ["];
Id 	:	[a-zA-Z_] [a-zA-Z_0-9]*;

Comment
 : ('//' ~[\r\n]* | '/*' .*? '*/') -> skip
 ;
Space
 : [ \t\r\n\u000C] -> skip
 ;