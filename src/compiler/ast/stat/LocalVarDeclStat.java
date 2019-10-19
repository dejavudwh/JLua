package compiler.ast.stat;

import compiler.ast.Exp;
import compiler.ast.Stat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
*   local nameLIst ['=' explist]
*   namelist ::= name {',' name}
*   explist ::= exp {',' exp}
 */

@Getter
@Setter
public class LocalVarDeclStat extends Stat {

    private List<String> nameList;
    private List<Exp> expList;
}
