package compiler.ast.exps;

import compiler.ast.Exp;
import compiler.ast.PrefixExp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableAccessExp extends PrefixExp {

    private Exp prefixExp;
    private Exp keyExp;
}
