package compiler.ast.stat;

import compiler.ast.Stat;
import compiler.ast.exps.FuncCallExp;

public class FuncCallStat extends Stat {

    private FuncCallExp exp;

    public FuncCallStat(FuncCallExp exp) {
        this.exp = exp;
    }
}
