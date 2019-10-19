package compiler.parser;

import compiler.ast.Block;
import compiler.ast.Exp;
import compiler.ast.Stat;
import compiler.ast.exps.IntegerExp;
import compiler.ast.exps.TrueExp;
import compiler.ast.stat.*;
import compiler.lexer.Lexer;

import java.util.ArrayList;
import java.util.List;

import static compiler.lexer.TokenKind.*;
import static compiler.parser.BlockParser.parseBlock;
import static compiler.parser.ExpParser.parseExpList;
import static compiler.parser.ExpParser.parseExp;

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

    // ;
    private static EmptyStat parseEmptyStat(Lexer lexer) {
        lexer.nextTokenOfKind(TOKEN_SEP_SEMI);
        return EmptyStat.INSTANCE;
    }

    // break
    private static BreakStat parseBreakStat(Lexer lexer) {
        lexer.nextTokenOfKind(TOKEN_KW_BREAK);
        return new BreakStat(lexer.line());
    }

    // '::' Name '::'
    private static LabelStat parseLabelStat(Lexer lexer) {
        lexer.nextTokenOfKind(TOKEN_SEP_LABEL);
        String name = lexer.nextIdentifier().getValue();
        lexer.nextTokenOfKind(TOKEN_SEP_LABEL);
        return new LabelStat(name);
    }

    // goto name
    private static GotoStat parseGotoStat(Lexer lexer) {
        lexer.nextTokenOfKind(TOKEN_KW_GOTO);
        String name = lexer.nextIdentifier().getValue();
        return new GotoStat(name);
    }

    // do block end
    private static DoStat parseDoStat(Lexer lexer) {
        lexer.nextTokenOfKind(TOKEN_KW_DO);
        Block block = parseBlock(lexer);
        lexer.nextTokenOfKind(TOKEN_KW_END);
        return new DoStat(block);
    }

    // while exp do block end
    private static Stat parseWhileStat(Lexer lexer) {
        lexer.nextTokenOfKind(TOKEN_KW_WHILE);
        Exp exp = parseExp(lexer);
        lexer.nextTokenOfKind(TOKEN_KW_DO);
        Block block = parseBlock(lexer);
        lexer.nextTokenOfKind(TOKEN_KW_END);
        return new WhileStat(exp, block);
    }

    // repeat block until exp
    private static Stat parseRepeatStat(Lexer lexer) {
        lexer.nextTokenOfKind(TOKEN_KW_REPEAT);
        Block block = parseBlock(lexer);
        lexer.nextTokenOfKind(TOKEN_KW_UNTIL);
        Exp exp = parseExp(lexer);
        return new RepeatStat(block, exp);
    }

    // if exp then block {elseif exp then block} [else block] end
    private static Stat parseIfStat(Lexer lexer) {
        List<Exp> exps = new ArrayList<>();
        List<Block> blocks = new ArrayList<>();

        lexer.nextTokenOfKind(TOKEN_KW_IF);
        exps.add(parseExp(lexer));
        lexer.nextTokenOfKind(TOKEN_KW_THEN);
        blocks.add(parseBlock(lexer));

        while (lexer.lookAhead() == TOKEN_KW_ELSEIF) {
            lexer.nextToken();
            exps.add(parseExp(lexer));
            lexer.nextTokenOfKind(TOKEN_KW_THEN);
            blocks.add(parseBlock(lexer));
        }

        if (lexer.lookAhead() == TOKEN_KW_ELSE) {
            lexer.nextToken();
            exps.add(new TrueExp(lexer.line()));
            blocks.add(parseBlock(lexer));
        }

        lexer.nextTokenOfKind(TOKEN_KW_END);
        return new IfStat(exps, blocks);
    }

    // for Name '=' exp ',' exp [',' exp] do block end
    // for namelist in explist do block end
    private static Stat parseForStat(Lexer lexer) {
        int lineOfFor = lexer.nextTokenOfKind(TOKEN_KW_FOR).getLine();
        String name = lexer.nextIdentifier().getValue();
        if (lexer.lookAhead() == TOKEN_OP_ASSIGN) {
            return finishForNumStat(lexer, name, lineOfFor);
        } else {
            return finishForInStat(lexer, name);
        }
    }

    // for Name '=' exp ',' exp [',' exp] do block end
    private static ForNumStat finishForNumStat(Lexer lexer, String name, int lineOfFor) {
        ForNumStat stat = new ForNumStat();
        stat.setLineOfFor(lineOfFor);
        stat.setVarName(name);

        lexer.nextTokenOfKind(TOKEN_OP_ASSIGN);
        stat.setInitExp(parseExp(lexer));
        lexer.nextTokenOfKind(TOKEN_SEP_COMMA);
        stat.setLimitExp(parseExp(lexer));

        if (lexer.lookAhead() == TOKEN_SEP_COMMA) {
            lexer.nextToken();
            stat.setStepExp(parseExp(lexer));
        } else {
            stat.setStepExp(new IntegerExp(lexer.line(), 1));
        }

        lexer.nextTokenOfKind(TOKEN_KW_DO);
        stat.setLineOfDo(lexer.line());
        stat.setBlock(parseBlock(lexer));
        lexer.nextTokenOfKind(TOKEN_KW_END);

        return stat;
    }

    // for namelist in explist do block end
    // namelist ::= Name {‘,’ Name}
    // explist ::= exp {‘,’ exp}
    private static ForInStat finishForInStat(Lexer lexer, String name0) {
        ForInStat stat = new ForInStat();

        stat.setNameList(finishNameList(lexer, name0));
        lexer.nextTokenOfKind(TOKEN_KW_IN);
        stat.setExpList(parseExpList(lexer));
        lexer.nextTokenOfKind(TOKEN_KW_DO);
        stat.setLineOfDo(lexer.line());
        stat.setBlock(parseBlock(lexer));
        lexer.nextTokenOfKind(TOKEN_KW_END);

        return stat;
    }

    // namelist ::= Name {',' Name}
    private static List<String> finishNameList(Lexer lexer, String name0) {
        List<String> names = new ArrayList<>();
        names.add(name0);
        while (lexer.lookAhead() == TOKEN_SEP_COMMA) {
            lexer.nextToken();
            names.add(lexer.nextIdentifier().getValue());
        }
        return names;
    }

    private static Stat parseFuncDefStat(Lexer lexer) {
    }

    private static Stat parseLocalAssignOrFuncDefStat(Lexer lexer) {
    }

    private static Stat parseAssignOrFuncCallStat(Lexer lexer) {
    }

}
