package com.company.logic.common;

/**
 * @author Ravi Mohan
 */
public interface LogicTokenTypes {
    int SYMBOL = 1;

    int LPAREN = 2; // (

    int RPAREN = 3; // )

    int LSQRBRACKET = 4; // [

    int RSQRBRACKET = 5; // ]

    int COMMA = 6;

    int CONNECTIVE = 7;

    int QUANTIFIER = 8;

    int PREDICATE = 9;

    int FUNCTION = 10;

    int VARIABLE = 11;

    int CONSTANT = 12;

    int TRUE = 13;

    int FALSE = 14;

    int EQUALS = 15;

    int WHITESPACE = 1000;

    int EOI = 9999; // End of Input.
}
