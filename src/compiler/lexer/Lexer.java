package compiler.lexer;

public class Lexer {

    private CharSeq chunk;
    private String chunkName;
    private int line;

    public Lexer(String chunk, String chunkName) {
        this.chunk = new CharSeq(chunk);
        this.chunkName = chunkName;
        this.line = 1;
    }
}
