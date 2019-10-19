package compiler.ast.stat;

import compiler.ast.Stat;

public class BreakStat extends Stat {

    public BreakStat(int line) {
        setLine(line);
    }
}
