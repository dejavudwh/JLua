package compiler.codegen;

import compiler.ast.Stat;
import compiler.ast.stat.*;
import static compiler.codegen.ExpProcessor.*;
import static compiler.codegen.BlockProcessor.*;

public class StatProcessor {

    static void processStat(FuncInfo fi, Stat node) {
        if (node instanceof FuncCallStat) {
            processFuncCallStat(fi, (FuncCallStat) node);
        } else if (node instanceof BreakStat) {
            processBreakStat(fi, (BreakStat) node);
        } else if (node instanceof DoStat) {
            processDoStat(fi, (DoStat) node);
        } else if (node instanceof WhileStat) {
            processWhileStat(fi, (WhileStat) node);
        } else if (node instanceof RepeatStat) {
            processRepeatStat(fi, (RepeatStat) node);
        } else if (node instanceof IfStat) {
            processIfStat(fi, (IfStat) node);
        } else if (node instanceof ForNumStat) {
            processForNumStat(fi, (ForNumStat) node);
        } else if (node instanceof ForInStat) {
            processForInStat(fi, (ForInStat) node);
        } else if (node instanceof AssignStat) {
            processAssignStat(fi, (AssignStat) node);
        } else if (node instanceof LocalVarDeclStat) {
            processLocalVarDeclStat(fi, (LocalVarDeclStat) node);
        } else if (node instanceof LocalFuncDefStat) {
            processLocalFuncDefStat(fi, (LocalFuncDefStat) node);
        } else if (node instanceof LabelStat
                || node instanceof GotoStat) {
            throw new RuntimeException("label and goto statements are not supported!");
        }
    }

    private static void processFuncCallStat(FuncInfo fi, FuncCallStat node) {
        int r = fi.allocReg();
        processFuncCallExp(fi, node.getExp(), r, 0);
        fi.freeReg();
    }

    private static void processBreakStat(FuncInfo fi, BreakStat node) {
        int pc = fi.emitJmp(node.getLine(), 0, 0);
        fi.addBreakJmp(pc);
    }

    private static void processDoStat(FuncInfo fi, DoStat node) {
        fi.enterScope(false);
        processBlock(fi, node.getBlock());
        fi.closeOpenUpvals(node.getBlock().getLastLine());
        fi.exitScope(fi.pc() + 1);
    }

    private static void processWhileStat(FuncInfo fi, WhileStat node) {
//        int pcBeforeExp = fi.pc();
//
//        int oldRegs = fi.usedRegs;
//        int a = expToOpArg(fi, node.getExp(), ARG_REG).arg;
//        fi.usedRegs = oldRegs;
    }

    private static void processRepeatStat(FuncInfo fi, RepeatStat node) {
    }

    private static void processIfStat(FuncInfo fi, IfStat node) {
    }

    private static void processForNumStat(FuncInfo fi, ForNumStat node) {
    }

    private static void processForInStat(FuncInfo fi, ForInStat node) {
    }

    private static void processAssignStat(FuncInfo fi, AssignStat node) {
    }

    private static void processLocalVarDeclStat(FuncInfo fi, LocalVarDeclStat node) {
    }

    private static void processLocalFuncDefStat(FuncInfo fi, LocalFuncDefStat node) {
        int r = fi.addLocVar(node.getName(), fi.pc() + 2);
        processFuncDefExp(fi, node.getExp(), r);
    }
}
