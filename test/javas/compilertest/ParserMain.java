package javas.compilertest;

import com.google.gson.GsonBuilder;
import compiler.ast.Block;
import compiler.parser.Parser;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ParserMain {

    public static void main(String[] args) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get("test/resources/hello_world.lua"));
        testParser(new String(data), "hello_world");
    }

    private static void testParser(String chunk, String chunkName) {
        Block block = Parser.parse(chunk, chunkName);
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(block);
        System.out.println(json);
    }
}
