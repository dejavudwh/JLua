package compiler.parser;

import compiler.ast.Stat;
import compiler.lexer.Lexer;

public class StatParser {

    /*
    stat ::=  ‘;’
        | break
        | ‘::’ Name ‘::’
        | goto Name
        | do block end
        | while exp do block end
        | repeat block until exp
        | if exp then block {elseif exp then block} [else block] end
        | for Name ‘=’ exp ‘,’ exp [‘,’ exp] do block end
        | for namelist in explist do block end
        | function funcname funcbody
        | local function Name funcbody
        | local namelist [‘=’ explist]
        | varlist ‘=’ explist
        | functioncall
    */
    static Stat parseStat(Lexer lexer) {
        switch (lexer.lookAhead()) {
            case TOKEN_SEP_SEMI:    return parseEmptyStat(lexer);
            case TOKEN_KW_BREAK:    return parseBreakStat(lexer);
            case TOKEN_SEP_LABEL:   return parseLabelStat(lexer);
            case TOKEN_KW_GOTO:     return parseGotoStat(lexer);
            case TOKEN_KW_DO:       return parseDoStat(lexer);
            case TOKEN_KW_WHILE:    return parseWhileStat(lexer);
            case TOKEN_KW_REPEAT:   return parseRepeatStat(lexer);
            case TOKEN_KW_IF:       return parseIfStat(lexer);
            case TOKEN_KW_FOR:      return parseForStat(lexer);
            case TOKEN_KW_FUNCTION: return parseFuncDefStat(lexer);
            case TOKEN_KW_LOCAL:    return parseLocalAssignOrFuncDefStat(lexer);
            default:                return parseAssignOrFuncCallStat(lexer);
        }
    }

    private static Stat parseEmptyStat(Lexer lexer) {
    }

    private static Stat parseBreakStat(Lexer lexer) {
    }

    private static Stat parseLabelStat(Lexer lexer) {
    }

    private static Stat parseGotoStat(Lexer lexer) {
    }

    private static Stat parseDoStat(Lexer lexer) {
    }

    private static Stat parseWhileStat(Lexer lexer) {
    }

    private static Stat parseRepeatStat(Lexer lexer) {
    }

    private static Stat parseIfStat(Lexer lexer) {
    }

    private static Stat parseForStat(Lexer lexer) {
    }

    private static Stat parseFuncDefStat(Lexer lexer) {
    }

    private static Stat parseLocalAssignOrFuncDefStat(Lexer lexer) {
    }

    private static Stat parseAssignOrFuncCallStat(Lexer lexer) {
    }

}
