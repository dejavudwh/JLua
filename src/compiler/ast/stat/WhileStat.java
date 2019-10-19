package compiler.ast.stat;

import compiler.ast.Block;
import compiler.ast.Exp;
import compiler.ast.Stat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WhileStat extends Stat {

    private Exp exp;
    private Block block;

    public WhileStat(Exp exp, Block block) {
        this.exp = exp;
        this.block = block;
    }
}
