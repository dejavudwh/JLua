package compiler.lexer;

public class Escaper {

    private CharSeq rawStr;
    private Lexer lexer;
    private StringBuilder buf = new StringBuilder();

    Escaper(String rawStr, Lexer lexer) {
        this.rawStr = new CharSeq(rawStr);
        this.lexer = lexer;
    }

    String escape() {

    }
}
