package compiler.parser;

import compiler.ast.Exp;
import compiler.ast.exps.*;
import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.lexer.TokenKind;

import java.util.Collections;
import java.util.List;

import static compiler.lexer.TokenKind.*;
import static compiler.parser.ExpParser.parseExp;
import static compiler.parser.ExpParser.parseExpList;

public class PrefixExpParser {

    /*
    prefixexp ::= Name
        | ‘(’ exp ‘)’
        | prefixexp ‘[’ exp ‘]’
        | prefixexp ‘.’ Name
        | prefixexp [‘:’ Name] args
    */
    static Exp parsePrefixExp(Lexer lexer) {
        Exp exp;
        if (lexer.lookAhead() == TokenKind.TOKEN_IDENTIFIER) {
            Token id = lexer.nextToken();
            exp = new NameExp(id.getLine(), id.getValue());
        } else {
            exp = parseParensExp(lexer);
        }
        return finishPrefixExp(lexer, exp);
    }

    private static Exp parseParensExp(Lexer lexer) {
        lexer.nextTokenOfKind(TokenKind.TOKEN_SEP_LPAREN);
        Exp exp = parseExp(lexer);
        lexer.nextTokenOfKind(TokenKind.TOKEN_SEP_RPAREN);

        // need to keep the parenthesis information
        if (exp instanceof VarargExp
                || exp instanceof FuncCallExp
                || exp instanceof NameExp
                || exp instanceof TableAccessExp) {
            return new ParensExp(exp);
        }

        return exp;
    }

    private static Exp finishPrefixExp(Lexer lexer, Exp exp) {
        while (true) {
            switch (lexer.lookAhead()) {
                case TOKEN_SEP_LBRACK: {
                    lexer.nextToken();
                    Exp keyExp = parseExp(lexer);
                    lexer.nextTokenOfKind(TokenKind.TOKEN_SEP_RBRACK);
                    exp = new TableAccessExp(lexer.line(), exp, keyExp);
                    break;
                }
                case TOKEN_SEP_DOT: {
                    lexer.nextToken();
                    Token name = lexer.nextIdentifier();
                    Exp keyExp = new StringExp(name);
                    exp = new TableAccessExp(name.getLine(), exp, keyExp);
                    break;
                }
                case TOKEN_SEP_COLON:
                case TOKEN_SEP_LPAREN:
                case TOKEN_SEP_LCURLY:
                case TOKEN_STRING:
                    exp = finishFuncCallExp(lexer, exp);
                    break;
                default:
                    return exp;
            }
        }
    }

    // functioncall ::=  prefixexp args | prefixexp ‘:’ Name args
    private static FuncCallExp finishFuncCallExp(Lexer lexer, Exp prefixExp) {
        FuncCallExp fcExp = new FuncCallExp();
        fcExp.setPrefixExp(prefixExp);
        fcExp.setNameExp(parseNameExp(lexer));
        fcExp.setLine(lexer.line());
        fcExp.setArgs(parseArgs(lexer));
        fcExp.setLastLine(lexer.line());
        return fcExp;
    }

    private static StringExp parseNameExp(Lexer lexer) {
        if (lexer.lookAhead() == TOKEN_SEP_COLON) {
            lexer.nextToken();
            Token name = lexer.nextIdentifier();
            return new StringExp(name);
        }
        return null;
    }

    // args ::=  ‘(’ [explist] ‘)’ | tableconstructor | LiteralString
    private static List<Exp> parseArgs(Lexer lexer) {
        switch (lexer.lookAhead()) {
            case TOKEN_SEP_LPAREN:
                lexer.nextToken();
                List<Exp> args = null;
                if (lexer.lookAhead() != TOKEN_SEP_RPAREN) {
                    args = parseExpList(lexer);
                }
                lexer.nextTokenOfKind(TOKEN_SEP_RPAREN);
                return args;
            case TOKEN_SEP_LCURLY:
                return Collections.singletonList(parseTableConstructorExp(lexer));
            default:
                Token str = lexer.nextTokenOfKind(TOKEN_STRING);
                return Collections.singletonList(new StringExp(str));
        }
    }

    // tableconstructor ::= ‘{’ [fieldlist] ‘}’
    static TableConstructorExp parseTableConstructorExp(Lexer lexer) {

    }
}
