package javas;

import api.LuaState;
import state.LuaStateImpl;

import java.nio.file.Files;
import java.nio.file.Paths;

public class LuaCallMain {
    public static void main(String[] args) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get("test/resources/ct.luac"));
        LuaState ls = new LuaStateImpl();
        ls.load(data, "ct.luac", "b");
        ls.call(0, 0);
    }
}
