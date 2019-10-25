package compiler.parser;

import compiler.ast.Block;
import compiler.ast.Exp;
import compiler.ast.Stat;
import compiler.ast.stat.EmptyStat;
import compiler.lexer.Lexer;
import compiler.lexer.TokenKind;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static compiler.parser.StatParser.parseStat;
import static compiler.parser.ExpParser.parseExpList;

public class BlockParser {

    // block ::= {stat} [retstat]
    static Block parseBlock(Lexer lexer) {
        Block block = new Block();
        block.setStats(parseStats(lexer));
        block.setRetExps(parseRetExps(lexer));
        block.setLastLine(lexer.line());
        return block;
    }

    private static List<Stat> parseStats(Lexer lexer) {
        List<Stat> stats = new ArrayList<>();
        while (!isReturnOrBlockEnd(lexer.lookAhead())) {
            Stat stat = parseStat(lexer);
            if (!(stat instanceof EmptyStat)) {
                stats.add(stat);
            }
        }
        return stats;
    }

    private static boolean isReturnOrBlockEnd(TokenKind kind) {
        switch (kind) {
            case TOKEN_KW_RETURN:
            case TOKEN_EOF:
            case TOKEN_KW_END:
            case TOKEN_KW_ELSE:
            case TOKEN_KW_ELSEIF:
            case TOKEN_KW_UNTIL:
                return true;
            default:
                return false;
        }
    }

    // retstat ::= return [explist] [';']
    // explist ::= exp {',' exp}
    private static List<Exp> parseRetExps(Lexer lexer) {
        if (lexer.lookAhead() != TokenKind.TOKEN_KW_RETURN) {
            return null;
        }

        lexer.nextToken();
        switch (lexer.lookAhead()) {
            case TOKEN_EOF:
            case TOKEN_KW_END:
            case TOKEN_KW_ELSE:
            case TOKEN_KW_ELSEIF:
            case TOKEN_KW_UNTIL:
                return Collections.emptyList();
            case TOKEN_SEP_SEMI:
                lexer.nextToken();
                return Collections.emptyList();
            default:
                List<Exp> exps = parseExpList(lexer);
                if (lexer.lookAhead() == TokenKind.TOKEN_SEP_SEMI) {
                    lexer.nextToken();
                }
                return exps;
        }
    }
}
