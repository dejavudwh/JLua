package compiler.ast.stat;

import compiler.ast.Stat;

public class GotoStat extends Stat {

    private String name;

    public GotoStat(String name) {
        this.name = name;
    }
}
