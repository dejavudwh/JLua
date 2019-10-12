package javas;

import api.LuaState;
import state.LuaStateImpl;

import java.nio.file.Files;
import java.nio.file.Paths;

public class CallJavaMain {

    public static void main(String[] args) throws Exception {
        // closure
        byte[] data = Files.readAllBytes(Paths.get("test/resources/ct.luac"));
        LuaState ls = new LuaStateImpl();
        ls.register("print", CallJavaMain::print);
        ls.load(data, "ct.luac", "b");
        ls.call(0, 0);
    }

    private static int print(LuaState ls) {
        int nArgs = ls.getTop();
        for (int i = 1; i <= nArgs; i++) {
            if (ls.isBoolean(i)) {
                System.out.print(ls.toBoolean(i));
            } else if (ls.isString(i)) {
                System.out.print(ls.toString(i));
            } else {
                System.out.print(ls.typeName(ls.type(i)));
            }
            if (i < nArgs) {
                System.out.print("\t");
            }
        }
        System.out.println();
        return 0;
    }
}
