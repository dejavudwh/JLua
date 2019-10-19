package compiler.ast.stat;

import compiler.ast.Stat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelStat extends Stat {

    private String name;

    public LabelStat(String name) {
        this.name = name;
    }

}
