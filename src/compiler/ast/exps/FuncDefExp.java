package compiler.ast.exps;

import compiler.ast.Block;
import compiler.ast.Exp;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
*   functiondef ::= function funcbody
*   funcbody ::= '(' [parlist] ')' block end
*   parlist ::= namelist [',' '...'] | '...'
*   namelist ::= Name {',' Name}
 */

@Getter
@Setter
public class FuncDefExp extends Exp {

    private List<String> parList;
    private boolean IsVararg;
    private Block block;
}
