package compiler.ast.stat;

import compiler.ast.Block;
import compiler.ast.Exp;
import compiler.ast.Stat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RepeatStat extends Stat {

    private Block block;
    private Exp exp;
}
