package compiler;

import binchunk.Prototype;
import compiler.ast.Block;
import compiler.codegen.CodeGen;
import compiler.parser.Parser;

public class Compiler {

    public static Prototype compile(String chunk, String chunkName) {
        Block ast = Parser.parse(chunk, chunkName);
        Prototype proto = CodeGen.genProto(ast);
        setSource(proto, chunkName);
        return proto;
    }

    private static void setSource(Prototype proto, String chunkName) {
        proto.setSource(chunkName);
        for (Prototype subProto : proto.getProtos()) {
            setSource(subProto, chunkName);
        }
    }
}
