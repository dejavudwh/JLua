package compiler.ast.stat;

import compiler.ast.Stat;
import compiler.ast.exps.FuncDefExp;
import lombok.Getter;
import lombok.Setter;

/*
*   function funcname funcbody
*   funcname ::= Name {'.' Name} [':' Name]
*   funcbody ::= '(' [parlist] ')' block end
*
*   parlist ::= namelist [',' '...'] | '...'
*   namelist ::= Name {',' Name}
*/

@Getter
@Setter
public class LocalFuncDefStat extends Stat {

    private String name;
    private FuncDefExp exp;
}
