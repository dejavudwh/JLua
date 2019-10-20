package compiler.ast.stat;

import compiler.ast.Exp;
import compiler.ast.Stat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
*   varlist '=' explist
*   varlist ::= var {',' var}
*   var ::= Name | prefixexp '[' exp ']' | prefixexp '.' Name
*   explist ::= exp {',' exp}
 */

@Getter
@Setter
public class AssignStat extends Stat {

    private List<Exp> varList;
    private List<Exp> expList;

    public AssignStat(int line, List<Exp> varList, List<Exp> expList) {
        setLine(line);
        this.varList = varList;
        this.expList = expList;
    }
}
