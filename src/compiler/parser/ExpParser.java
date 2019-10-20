package compiler.parser;

import compiler.ast.Block;
import compiler.ast.Exp;
import compiler.ast.exps.BinopExp;
import compiler.ast.exps.FuncDefExp;
import compiler.lexer.Lexer;
import compiler.lexer.Token;

import java.util.ArrayList;
import java.util.List;

import static compiler.lexer.TokenKind.*;
import static compiler.parser.Optimizer.*;
import static compiler.parser.BlockParser.parseBlock;

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
        return parseExp12(lexer);
    }

    // or
    private static Exp parseExp12(Lexer lexer) {
        Exp exp = parseExp11(lexer);
        while (lexer.lookAhead() == TOKEN_OP_OR) {
            Token op = lexer.nextToken();
            BinopExp lor = new BinopExp(op, exp, parseExp11(lexer));
            exp = optimizeLogicalOr(lor);
        }
        return exp;
    }

    // and
    private static Exp parseExp11(Lexer lexer) {

    }

    // functiondef ::= function funcbody
    // funcbody ::= ‘(’ [parlist] ‘)’ block end
    static FuncDefExp parseFuncDefExp(Lexer lexer) {
        int line = lexer.line();
        lexer.nextTokenOfKind(TOKEN_SEP_LPAREN);
        List<String> parList = parseParList(lexer);
        lexer.nextTokenOfKind(TOKEN_SEP_RPAREN);
        Block block = parseBlock(lexer);
        lexer.nextTokenOfKind(TOKEN_KW_END);
        int lastLine = lexer.line();

        FuncDefExp fdExp = new FuncDefExp();
        fdExp.setLine(line);
        fdExp.setLastLine(lastLine);
        fdExp.setIsVararg(parList.remove("..."));
        fdExp.setParList(parList);
        fdExp.setBlock(block);
        return fdExp;
    }

    // [parlist]
    // parlist ::= namelist [',' '...'] | '...'
    private static List<String> parseParList(Lexer lexer) {
        List<String> names = new ArrayList<>();

        switch (lexer.lookAhead()) {
            case TOKEN_SEP_RPAREN:
                return names;
            case TOKEN_VARARG:
                lexer.nextToken();
                names.add("...");
                return names;
        }

        names.add(lexer.nextIdentifier().getValue());
        while (lexer.lookAhead() == TOKEN_SEP_COMMA) {
            lexer.nextToken();
            if (lexer.lookAhead() == TOKEN_IDENTIFIER) {
                names.add(lexer.nextIdentifier().getValue());
            } else {
                lexer.nextTokenOfKind(TOKEN_VARARG);
                names.add("...");
                break;
            }
        }

        return names;
    }
}
