package compiler.ast.stat;

import compiler.ast.Block;
import compiler.ast.Exp;
import compiler.ast.Stat;
import lombok.Getter;
import lombok.Setter;

/*
*   for Name '=' exp ',' exp [',' exp] do block end
 */

@Getter
@Setter
public class ForNumStat extends Stat {

    private int lineOfFor;
    private int lineOfDo;
    private String varName;
    private Exp InitExp;
    private Exp limitExp;
    private Exp StepExp;
    private Block Block;
}
