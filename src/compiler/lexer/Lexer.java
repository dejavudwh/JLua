package compiler.lexer;

import java.util.regex.Pattern;

public class Lexer {

    //  // is to escape in Java
    private static final Pattern reOpeningLongBracket = Pattern.compile("^\\[=*\\[");

    private CharSeq chunk;
    private String chunkName;
    private int line;
    // to support lookahead
    private Token cachedNextToken;
    private int lineBackup;

    public Lexer(String chunk, String chunkName) {
        this.chunk = new CharSeq(chunk);
        this.chunkName = chunkName;
        this.line = 1;
    }

    private void skipWhiteSpaces() {
        while (chunk.length() > 0) {
            if (chunk.startsWith("--")) {
                skipComment();
            }
        }
    }

    private void skipComment() {
        chunk.next(2);

        //long comment
        if (chunk.startsWith("[")) {
            if (chunk.find(reOpeningLongBracket) != null) {
                scanLongString();
                return;
            }
        }

        // short comment
        while (chunk.length() > 0 && !CharUtil.isNewLine(chunk.charAt(0))) {
            chunk.next(1);
        }
    }

    private String scanLongString() {
        String openingLongBracket = chunk.find(reOpeningLongBracket);
        if (openingLongBracket == null) {
            return error("invalid long string delimiter near '%s'",
                    chunk.substring(0, 2));
        }
    }

    <T> T error(String fmt, Object... args) {
        String msg = String.format(fmt, args);
        msg = String.format("%s:%d: %s", chunkName, line(), msg);
        throw new RuntimeException(msg);
    }

    public int line() {
        return cachedNextToken != null ? lineBackup : line;
    }
}
