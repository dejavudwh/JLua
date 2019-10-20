package compiler.ast.exps;

import compiler.ast.Exp;
import compiler.lexer.Token;
import compiler.lexer.TokenKind;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BinopExp extends Exp {

    private TokenKind op;
    private Exp exp1;
    private Exp exp2;

    public BinopExp(Token op, Exp exp1, Exp exp2) {
        setLine(op.getLine());
        this.exp1 = exp1;
        this.exp2 = exp2;

        if (op.getKind() == TokenKind.TOKEN_OP_MINUS) {
            this.op = TokenKind.TOKEN_OP_SUB;
        } else if (op.getKind() == TokenKind.TOKEN_OP_WAVE) {
            this.op = TokenKind.TOKEN_OP_BXOR;
        } else {
            this.op = op.getKind();
        }
    }
}
