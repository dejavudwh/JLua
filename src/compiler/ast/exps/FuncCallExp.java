package compiler.ast.exps;

import compiler.ast.Exp;
import compiler.ast.PrefixExp;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
*   functioncall ::= prefixexp [':' Name] args
*   args ::= '(' [explist] ')' | tableconstructor | LiteralString
 */

@Getter
@Setter
public class FuncCallExp extends PrefixExp {

    private Exp prefixExp;
    private StringExp nameExp;
    private List<Exp> args;
}
