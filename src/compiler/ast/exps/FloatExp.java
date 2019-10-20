package compiler.ast.exps;

import compiler.ast.Exp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FloatExp extends Exp {

    private double val;

    public FloatExp(int line, Double val) {
        setLine(line);
        this.val = val;
    }
}
