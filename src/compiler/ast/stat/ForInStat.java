package compiler.ast.stat;

import compiler.ast.Block;
import compiler.ast.Exp;
import compiler.ast.Stat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
*   for namelist in explist do block end
 */

@Getter
@Setter
public class ForInStat extends Stat {

    private int lineOfDo;
    private List<String> nameList;
    private List<Exp> expList;
    private Block block;
}
