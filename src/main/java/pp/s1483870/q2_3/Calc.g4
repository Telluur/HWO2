grammar Calc;

@header{package pp.s1483870.q2_3;}

/** Complete expression. */
complete
	: expr EOF
	;

/** Recursively defined expression. */
expr : MINUS expr      # minus
     | expr TIMES expr # times
     | expr PLUS expr  # plus
     | LPAR expr RPAR  # par
     | NUMBER          # number
     ;

TIMES  : '*';
PLUS   : '+';
MINUS  : '-';
LPAR   : '(';
RPAR   : ')';

NUMBER : [0-9]+;

// ignore whitespace
WS : [ \t\n\r] -> skip;
     