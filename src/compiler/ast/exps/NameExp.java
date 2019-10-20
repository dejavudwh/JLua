package compiler.ast.exps;

import compiler.ast.Exp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NameExp extends Exp {

    private String name;

    public NameExp(int line, String name) {
        setLine(line);
        this.name = name;
    }
}
