package compiler.codegen;

import compiler.ast.Exp;
import compiler.ast.exps.*;

public class ExpProcessor {

    // kind of operands
    static final int ARG_CONST = 1; // const index
    static final int ARG_REG   = 2; // register index
    static final int ARG_UPVAL = 4; // upvalue index
    static final int ARG_RK    = ARG_REG | ARG_CONST;
    static final int ARG_RU    = ARG_REG | ARG_UPVAL;
    //static final int ARG_RUK   = ARG_REG | ARG_UPVAL | ARG_CONST;

    static class ArgAndKind {
        int arg;
        int kind;
    }

    static void processExp(FuncInfo fi, Exp node, int a, int n) {
        if (node instanceof NilExp) {
            fi.emitLoadNil(node.getLine(), a, n);
        } else if (node instanceof FalseExp) {
            fi.emitLoadBool(node.getLine(), a, 0, 0);
        } else if (node instanceof TrueExp) {
            fi.emitLoadBool(node.getLine(), a, 1, 0);
        } else if (node instanceof IntegerExp) {
            fi.emitLoadK(node.getLine(), a, ((IntegerExp) node).getVal());
        } else if (node instanceof FloatExp) {
            fi.emitLoadK(node.getLine(), a, ((FloatExp) node).getVal());
        } else if (node instanceof StringExp) {
            fi.emitLoadK(node.getLine(), a, ((StringExp) node).getStr());
        } else if (node instanceof ParensExp) {
            processExp(fi, ((ParensExp) node).getExp(), a, 1);
        } else if (node instanceof VarargExp) {
            processVarargExp(fi, (VarargExp) node, a, n);
        } else if (node instanceof FuncDefExp) {
            processFuncDefExp(fi, (FuncDefExp) node, a);
        } else if (node instanceof TableConstructorExp) {
            processTableConstructorExp(fi, (TableConstructorExp) node, a);
        } else if (node instanceof UnopExp) {
            processUnopExp(fi, (UnopExp) node, a);
        } else if (node instanceof BinopExp) {
            processBinopExp(fi, (BinopExp) node, a);
        } else if (node instanceof ConcatExp) {
            processConcatExp(fi, (ConcatExp) node, a);
        } else if (node instanceof NameExp) {
            processNameExp(fi, (NameExp) node, a);
        } else if (node instanceof TableAccessExp) {
            processTableAccessExp(fi, (TableAccessExp) node, a);
        } else if (node instanceof FuncCallExp) {
            processFuncCallExp(fi, (FuncCallExp) node, a, n);
        }
    }

    static void processVarargExp(FuncInfo fi, VarargExp node, int a, int n) {
    }

    static void processFuncDefExp(FuncInfo fi, FuncDefExp node, int a) {
    }

    static void processTableConstructorExp(FuncInfo fi, TableConstructorExp node, int a) {
    }

    static void processUnopExp(FuncInfo fi, UnopExp node, int a) {
    }

    static void processBinopExp(FuncInfo fi, BinopExp node, int a) {
    }

    static void processConcatExp(FuncInfo fi, ConcatExp node, int a) {
    }

    static void processNameExp(FuncInfo fi, NameExp node, int a) {
    }

    static void processTableAccessExp(FuncInfo fi, TableAccessExp node, int a) {
    }

    static void processFuncCallExp(FuncInfo fi, FuncCallExp node, int a, int n) {
    }

    static void processTailCallExp(FuncInfo fi, FuncCallExp node, int a) {
    }

    static ArgAndKind expToOpArg(FuncInfo fi, Exp node, int argKinds) {
    }
}
