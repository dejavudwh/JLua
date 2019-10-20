package compiler.ast.exps;

import compiler.ast.Exp;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/*
*   tableconstructor ::= '{' [fieldlist] '}'
*   fieldlist ::= field {fieldsep field} [fieldsep]
*   field ::= '[' exp ']' '=' exp | Name '=' exp | exp
*   fieldsep ::= ',' | ';'
 */

@Getter
@Setter
public class TableConstructorExp extends Exp {

    private List<Exp> keyExps = new ArrayList<>();
    private List<Exp> valExps = new ArrayList<>();

    public void addKey(Exp key) {
        keyExps.add(key);
    }

    public void addVal(Exp val) {
        valExps.add(val);
    }
}
