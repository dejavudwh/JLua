package compiler.ast.exps;

import compiler.ast.Exp;

public class NameExp extends Exp {

    private String name;

    public NameExp(int line, String name) {
        setLine(line);
        this.name = name;
    }
}
