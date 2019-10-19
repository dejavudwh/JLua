package compiler.ast.exps;

import compiler.ast.Exp;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
*   tableconstructor ::= '{' [fieldlist] '}'
*   fieldlist ::= field {fieldsep field} [fieldsep]
*   field ::= '[' exp ']' '=' exp | Name '=' exp | exp
*   fieldsep ::= ',' | ';'
 */

@Getter
@Setter
public class TableConstructorExp {

    private List<Exp> keyExps;
    private List<Exp> valExps;
}
