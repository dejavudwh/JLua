package compiler.ast.stat;

import compiler.ast.Block;
import compiler.ast.Exp;
import compiler.ast.Stat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IfStat extends Stat {

    private List<Exp> exp;
    private List<Block> blocks;
}
