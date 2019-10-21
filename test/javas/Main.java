package javas;

import api.LuaState;
import api.ThreadStatus;
import state.LuaStateImpl;

import static api.LuaType.*;
import static api.ThreadStatus.*;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get("test/resources/table_test.lua"));
        LuaState ls = new LuaStateImpl();
        ls.register("print", Main::print);
        ls.register("getmetatable", Main::getMetatable);
        ls.register("setmetatable", Main::setMetatable);
        ls.register("next", Main::next);
        ls.register("pairs", Main::pairs);
        ls.register("ipairs", Main::iPairs);
        ls.register("error", Main::error);
        ls.register("pcall", Main::pCall);
        ls.load(data, "hello_world", "bt");
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

    private static int getMetatable(LuaState ls) {
        if (!ls.getMetatable(1)) {
            ls.pushNil();
        }
        return 1;
    }

    private static int setMetatable(LuaState ls) {
        ls.setMetatable(1);
        return 1;
    }

    private static int next(LuaState ls) {
        ls.setTop(2); /* create a 2nd argument if there isn't one */
        if (ls.next(1)) {
            return 2;
        } else {
            ls.pushNil();
            return 1;
        }
    }

    private static int pairs(LuaState ls) {
        ls.pushJavaFunction(Main::next); /* will return generator, */
        ls.pushValue(1);                 /* state, */
        ls.pushNil();
        return 3;
    }

    private static int iPairs(LuaState ls) {
        ls.pushJavaFunction(Main::iPairsAux); /* iteration function */
        ls.pushValue(1);                      /* state */
        ls.pushInteger(0);                    /* initial value */
        return 3;
    }

    private static int iPairsAux(LuaState ls) {
        long i = ls.toInteger(2) + 1;
        ls.pushInteger(i);
        return ls.getI(1, i) == LUA_TNIL ? 1 : 2;
    }

    private static int  error(LuaState ls) {
        return ls.error();
    }

    private static int  pCall(LuaState ls) {
        int nArgs = ls.getTop() - 1;
        ThreadStatus status = ls.pCall(nArgs, -1, 0);
        ls.pushBoolean(status == LUA_OK);
        ls.insert(1);
        return ls.getTop();
    }
}
