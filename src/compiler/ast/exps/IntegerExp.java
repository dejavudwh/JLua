package compiler.ast.exps;

import compiler.ast.Exp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntegerExp extends Exp {

    private long val;

    public IntegerExp(int line, long val) {
        this.val = val;
        setLine(line);
    }
}
