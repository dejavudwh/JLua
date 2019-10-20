package compiler.parser;

import compiler.ast.Block;
import compiler.ast.Exp;
import compiler.ast.exps.*;
import compiler.lexer.Lexer;
import compiler.lexer.Token;
import number.LuaNumber;

import java.util.ArrayList;
import java.util.List;

import static compiler.lexer.TokenKind.*;
import static compiler.parser.Optimizer.*;
import static compiler.parser.BlockParser.parseBlock;
import static compiler.parser.PrefixExpParser.parsePrefixExp;

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
        Exp exp = parseExp10(lexer);
        while (lexer.lookAhead() == TOKEN_OP_AND) {
            Token op = lexer.nextToken();
            BinopExp land = new BinopExp(op, exp, parseExp10(lexer));
            exp = optimizeLogicalAnd(land);
        }
        return exp;
    }

    // compare
    private static Exp parseExp10(Lexer lexer) {
        Exp exp = parseExp9(lexer);
        while (true) {
            switch (lexer.lookAhead()) {
                case TOKEN_OP_LT:
                case TOKEN_OP_GT:
                case TOKEN_OP_NE:
                case TOKEN_OP_LE:
                case TOKEN_OP_GE:
                case TOKEN_OP_EQ:
                    Token op = lexer.nextToken();
                    exp = new BinopExp(op, exp, parseExp9(lexer));
                    break;
                default:
                    return exp;
            }
        }
    }

    // |
    private static Exp parseExp9(Lexer lexer) {
        Exp exp = parseExp8(lexer);
        while (lexer.lookAhead() == TOKEN_OP_BOR) {
            Token op = lexer.nextToken();
            BinopExp bor = new BinopExp(op, exp, parseExp8(lexer));
            exp = optimizeBitwiseBinaryOp(bor);
        }
        return exp;
    }

    // ~
    private static Exp parseExp8(Lexer lexer) {
        Exp exp = parseExp7(lexer);
        while (lexer.lookAhead() == TOKEN_OP_WAVE) {
            Token op = lexer.nextToken();
            BinopExp bxor = new BinopExp(op, exp, parseExp7(lexer));
            exp = optimizeBitwiseBinaryOp(bxor);
        }
        return exp;
    }

    // &
    private static Exp parseExp7(Lexer lexer) {
        Exp exp = parseExp6(lexer);
        while (lexer.lookAhead() == TOKEN_OP_BAND) {
            Token op = lexer.nextToken();
            BinopExp band = new BinopExp(op, exp, parseExp6(lexer));
            exp = optimizeBitwiseBinaryOp(band);
        }
        return exp;
    }

    // shift
    private static Exp parseExp6(Lexer lexer) {
        Exp exp = parseExp5(lexer);
        while (true) {
            switch (lexer.lookAhead()) {
                case TOKEN_OP_SHL:
                case TOKEN_OP_SHR:
                    Token op = lexer.nextToken();
                    BinopExp shx = new BinopExp(op, exp, parseExp5(lexer));
                    exp = optimizeBitwiseBinaryOp(shx);
                    break;
                default:
                    return exp;
            }
        }
    }

    // a .. b
    private static Exp parseExp5(Lexer lexer) {
        Exp exp = parseExp4(lexer);
        if (lexer.lookAhead() != TOKEN_OP_CONCAT) {
            return exp;
        }

        List<Exp> exps = new ArrayList<>();
        exps.add(exp);
        int line = 0;
        while (lexer.lookAhead() == TOKEN_OP_CONCAT) {
            line = lexer.nextToken().getLine();
            exps.add(parseExp4(lexer));
        }
        return new ConcatExp(line, exps);
    }

    // x +/- y
    private static Exp parseExp4(Lexer lexer) {
        Exp exp = parseExp3(lexer);
        while (true) {
            switch (lexer.lookAhead()) {
                case TOKEN_OP_ADD:
                case TOKEN_OP_MINUS:
                    Token op = lexer.nextToken();
                    BinopExp arith = new BinopExp(op, exp, parseExp3(lexer));
                    exp = optimizeArithBinaryOp(arith);
                    break;
                default:
                    return exp;
            }
        }
    }

    // *, %, /, //
    private static Exp parseExp3(Lexer lexer) {
        Exp exp = parseExp2(lexer);
        while (true) {
            switch (lexer.lookAhead()) {
                case TOKEN_OP_MUL:
                case TOKEN_OP_MOD:
                case TOKEN_OP_DIV:
                case TOKEN_OP_IDIV:
                    Token op = lexer.nextToken();
                    BinopExp arith = new BinopExp(op, exp, parseExp2(lexer));
                    exp = optimizeArithBinaryOp(arith);
                    break;
                default:
                    return exp;
            }
        }
    }

    // unary
    private static Exp parseExp2(Lexer lexer) {
        switch (lexer.lookAhead()) {
            case TOKEN_OP_MINUS:
            case TOKEN_OP_WAVE:
            case TOKEN_OP_LEN:
            case TOKEN_OP_NOT:
                Token op = lexer.nextToken();
                UnopExp exp = new UnopExp(op, parseExp2(lexer));
                return optimizeUnaryOp(exp);
        }
        return parseExp1(lexer);
    }

    // ^
    private static Exp parseExp1(Lexer lexer) { // pow is right associative
        Exp exp = parseExp0(lexer);
        if (lexer.lookAhead() == TOKEN_OP_POW) {
            Token op = lexer.nextToken();
            exp = new BinopExp(op, exp, parseExp2(lexer));
        }
        return optimizePow(exp);
    }

    private static Exp parseExp0(Lexer lexer) {
        switch (lexer.lookAhead()) {
            case TOKEN_VARARG: // ...
                return new VarargExp(lexer.nextToken().getLine());
            case TOKEN_KW_NIL: // nil
                return new NilExp(lexer.nextToken().getLine());
            case TOKEN_KW_TRUE: // true
                return new TrueExp(lexer.nextToken().getLine());
            case TOKEN_KW_FALSE: // false
                return new FalseExp(lexer.nextToken().getLine());
            case TOKEN_STRING: // LiteralString
                return new StringExp(lexer.nextToken());
            case TOKEN_NUMBER: // Numeral
                return parseNumberExp(lexer);
            case TOKEN_SEP_LCURLY: // tableconstructor
                return parseTableConstructorExp(lexer);
            case TOKEN_KW_FUNCTION: // functiondef
                lexer.nextToken();
                return parseFuncDefExp(lexer);
            default: // prefixexp
                return parsePrefixExp(lexer);
        }
    }

    private static Exp parseNumberExp(Lexer lexer) {
        Token token = lexer.nextToken();
        Long i = LuaNumber.parseInteger(token.getValue());
        if (i != null) {
            return new IntegerExp(token.getLine(), i);
        }
        Double f = LuaNumber.parseFloat(token.getValue());
        if (f != null) {
            return new FloatExp(token.getLine(), f);
        }
        throw new RuntimeException("not a number: " + token);
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
