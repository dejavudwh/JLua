package compiler.ast.exps;

import compiler.ast.Exp;

public class IntegerExp extends Exp {

    private long val;

    public IntegerExp(int line, long val) {
        this.val = val;
        setLine(line);
    }
}
