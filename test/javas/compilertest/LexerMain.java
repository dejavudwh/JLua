package javas.compilertest;

import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.lexer.TokenKind;

import static compiler.lexer.TokenKind.*;

import java.nio.file.Files;
import java.nio.file.Paths;

public class LexerMain {
    public static void main(String[] args) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get("test/resources/compiler/hello_world.lua"));
        testLexer(new String(data), "hello_world.lua");
    }

    private static void testLexer(String chunk, String chunkName) {
        Lexer lexer = new Lexer(chunk, chunkName);
        for (;;) {
            Token token = lexer.nextToken();
            System.out.printf("[%2d] [%-10s] %s\n",
                    token.getLine(), kindToCategory(token.getKind()), token.getValue());
            if (token.getKind() == TOKEN_EOF) {
                break;
            }
        }
    }

    private static String kindToCategory(TokenKind kind) {
        if (kind.ordinal() < TOKEN_SEP_SEMI.ordinal()) {
            return "other";
        }
        if (kind.ordinal() <= TOKEN_SEP_RCURLY.ordinal()) {
            return "separator";
        }
        if (kind.ordinal() <= TOKEN_OP_NOT.ordinal()) {
            return "operator";
        }
        if (kind.ordinal() <= TOKEN_KW_WHILE.ordinal()) {
            return "keyword";
        }
        if (kind == TOKEN_IDENTIFIER) {
            return "identifier";
        }
        if (kind == TOKEN_NUMBER) {
            return "number";
        }
        if (kind == TOKEN_STRING) {
            return "string";
        }
        return "other";
    }
}
