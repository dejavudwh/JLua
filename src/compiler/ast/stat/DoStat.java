package compiler.ast.stat;

import compiler.ast.Block;
import compiler.ast.Stat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoStat extends Stat {

    private Block block;

    public DoStat(Block block) {
        this.block = block;
    }
}
