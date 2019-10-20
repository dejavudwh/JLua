package compiler.parser;

import compiler.ast.Block;
import compiler.ast.Exp;
import compiler.ast.Stat;
import compiler.ast.exps.*;
import compiler.ast.stat.*;
import compiler.lexer.Lexer;
import compiler.lexer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static compiler.lexer.TokenKind.*;
import static compiler.parser.BlockParser.parseBlock;
import static compiler.parser.ExpParser.parseExpList;
import static compiler.parser.ExpParser.parseExp;
import static compiler.parser.ExpParser.parseFuncDefExp;
import static compiler.parser.PrefixExpParser.parsePrefixExp;

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

    // function funcname funcbody
    // funcname ::= Name {‘.’ Name} [‘:’ Name]
    // funcbody ::= ‘(’ [parlist] ‘)’ block end
    // parlist ::= namelist [‘,’ ‘...’] | ‘...’
    // namelist ::= Name {‘,’ Name}
    private static Stat parseFuncDefStat(Lexer lexer) {
        lexer.nextTokenOfKind(TOKEN_KW_FUNCTION);
        Map<Exp, Boolean> map = parseFuncName(lexer);
        Exp fnExp = map.keySet().iterator().next();
        boolean hasColon = map.values().iterator().next();
        FuncDefExp fdExp = parseFuncDefExp(lexer);
        if (hasColon) {
            if (fdExp.getParList() == null) {
                fdExp.setParList(new ArrayList<>());
            }
            fdExp.getParList().add(0, "self");
        }

        return new AssignStat(fdExp.getLastLine(),
                Collections.singletonList(fnExp),
                Collections.singletonList(fdExp));
    }

    // funcname ::= Name {',' Name} [':' Name]
    private static Map<Exp, Boolean> parseFuncName(Lexer lexer) {
        Token id = lexer.nextIdentifier();
        Exp exp = new NameExp(id.getLine(), id.getValue());
        boolean hasColon = false;

        while (lexer.lookAhead() == TOKEN_SEP_DOT) {
            lexer.nextToken();
            id = lexer.nextIdentifier();
            Exp idx = new StringExp(id);
            exp = new TableAccessExp(id.getLine(), exp, idx);
        }
        if (lexer.lookAhead() == TOKEN_SEP_COLON) {
            lexer.nextToken();
            id = lexer.nextIdentifier();
            Exp idx = new StringExp(id);
            exp = new TableAccessExp(id.getLine(), exp, idx);
            hasColon = true;
        }

        return Collections.singletonMap(exp, hasColon);
    }

    // local function Name funcbody
    // local namelist ['=' explist]
    private static Stat parseLocalAssignOrFuncDefStat(Lexer lexer) {
        lexer.nextTokenOfKind(TOKEN_KW_LOCAL);
        if (lexer.lookAhead() == TOKEN_KW_FUNCTION) {
            return finishLocalFuncDefStat(lexer);
        } else {
            return finishLocalVarDeclStat(lexer);
        }
    }

    // local function Name funcbody
    private static LocalFuncDefStat finishLocalFuncDefStat(Lexer lexer) {
        lexer.nextTokenOfKind(TOKEN_KW_FUNCTION);
        String name = lexer.nextIdentifier().getValue();
        FuncDefExp fdExp = parseFuncDefExp(lexer);
        return new LocalFuncDefStat(name, fdExp);
    }

    // local namelist [‘=’ explist]
    private static LocalVarDeclStat finishLocalVarDeclStat(Lexer lexer) {
        String name0 = lexer.nextIdentifier().getValue();
        List<String> nameList = finishNameList(lexer, name0);
        List<Exp> expList = null;
        if (lexer.lookAhead() == TOKEN_OP_ASSIGN) {
            lexer.nextToken();
            expList = parseExpList(lexer);
        }
        int lastLine = lexer.line();
        return new LocalVarDeclStat(lastLine, nameList, expList);
    }

    // varlist '=' explist
    // functioncall
    private static Stat parseAssignOrFuncCallStat(Lexer lexer) {
        Exp prefixExp = parsePrefixExp(lexer);
        if (prefixExp instanceof FuncCallExp) {
            return new FuncCallStat((FuncCallExp) prefixExp);
        } else {
            return parseAssignStat(lexer, prefixExp);
        }
    }

    // varlist '=' exlist |
    private static AssignStat parseAssignStat(Lexer lexer, Exp var0) {
        List<Exp> varList = finishVarList(lexer, var0);
        lexer.nextTokenOfKind(TOKEN_OP_ASSIGN);
        List<Exp> expList = parseExpList(lexer);
        int lastLine = lexer.line();
        return new AssignStat(lastLine, varList, expList);
    }

    // varlist ::= var {',' var}
    private static List<Exp> finishVarList(Lexer lexer, Exp var0) {
        List<Exp> vars = new ArrayList<>();
        while (lexer.lookAhead() == TOKEN_SEP_COMMA) {
            lexer.nextToken();
            Exp exp = parsePrefixExp(lexer);
            vars.add(checkVar(lexer, exp));
        }
        return vars;
    }

    // var ::=  Name | prefixexp ‘[’ exp ‘]’ | prefixexp ‘.’ Name
    private static Exp checkVar(Lexer lexer, Exp exp) {
        if (exp instanceof NameExp || exp instanceof TableAccessExp) {
            return exp;
        }
        lexer.nextTokenOfKind(null); // trigger error
        throw new RuntimeException("unreachable!");
    }

}
