package compiler.ast.exps;

import compiler.ast.Exp;
import compiler.lexer.Token;

public class StringExp extends Exp {

    private String str;

    public StringExp(Token token) {
        setLine(token.getLine());
        this.str = token.getValue();
    }

    public StringExp(int line, String str) {
        setLine(line);
        this.str = str;
    }
}
