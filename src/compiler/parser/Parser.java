package compiler.parser;

import compiler.ast.Block;
import compiler.lexer.Lexer;
import compiler.lexer.TokenKind;

public class Parser {

    public static Block parse(String chunk, String chunkName) {
        Lexer lexer = new Lexer(chunk, chunkName);
        Block block = BlockParser.parseBlock(lexer);
        lexer.nextTokenOfKind(TokenKind.TOKEN_EOF);
        return block;
    }
}
