package compiler.codegen;

import compiler.ast.Exp;
import compiler.ast.exps.FuncCallExp;
import compiler.ast.exps.VarargExp;

public class ExpHelper {

    static boolean isVarargOrFuncCall(Exp exp){
        return exp instanceof VarargExp
                || exp instanceof FuncCallExp;
    }
}
