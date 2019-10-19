package compiler.parser;

import compiler.ast.Exp;
import compiler.lexer.Lexer;

import java.util.ArrayList;
import java.util.List;

import static compiler.lexer.TokenKind.*;

public class ExpParser {

    // explist ::= exp {',' exp}
    static List<Exp> parseExpList(Lexer lexer) {
        List<Exp> exps = new ArrayList<>();
        while (lexer.lookAhead() == TOKEN_SEP_COMMA) {
            lexer.nextToken();
            exps.add(parseExp(lexer));
        }
        return exps;
    }

    /*
    exp ::=  nil | false | true | Numeral | LiteralString | ‘...’ | functiondef |
         prefixexp | tableconstructor | exp binop exp | unop exp
    */
    /*
    exp   ::= exp12
    exp12 ::= exp11 {or exp11}
    exp11 ::= exp10 {and exp10}
    exp10 ::= exp9 {(‘<’ | ‘>’ | ‘<=’ | ‘>=’ | ‘~=’ | ‘==’) exp9}
    exp9  ::= exp8 {‘|’ exp8}
    exp8  ::= exp7 {‘~’ exp7}
    exp7  ::= exp6 {‘&’ exp6}
    exp6  ::= exp5 {(‘<<’ | ‘>>’) exp5}
    exp5  ::= exp4 {‘..’ exp4}
    exp4  ::= exp3 {(‘+’ | ‘-’ | ‘*’ | ‘/’ | ‘//’ | ‘%’) exp3}
    exp2  ::= {(‘not’ | ‘#’ | ‘-’ | ‘~’)} exp1
    exp1  ::= exp0 {‘^’ exp2}
    exp0  ::= nil | false | true | Numeral | LiteralString
            | ‘...’ | functiondef | prefixexp | tableconstructor
    */
    static Exp parseExp(Lexer lexer) {

    }
}
