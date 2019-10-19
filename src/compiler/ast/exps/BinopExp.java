package compiler.ast.exps;

import compiler.ast.Exp;
import compiler.lexer.TokenKind;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BinopExp extends Exp {

    private TokenKind op;
    private Exp exp1;
    private Exp exp2;
}
