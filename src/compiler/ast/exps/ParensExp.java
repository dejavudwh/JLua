package compiler.ast.exps;

import compiler.ast.Exp;
import compiler.ast.PrefixExp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParensExp extends PrefixExp {

    private Exp exp;

    public ParensExp(Exp exp) {
        this.exp = exp;
    }
}
