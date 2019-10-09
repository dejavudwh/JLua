package javas;

import api.LuaState;
import api.LuaType;
import api.LuaVM;
import binchunk.BinaryChunk;
import binchunk.Prototype;
import state.LuaStateImpl;
import vm.Instruction;
import vm.OpCode;

import java.nio.file.Files;
import java.nio.file.Paths;

public class LuaMain {

    public static void main(String[] args) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get("test/resources/st.luac"));
        Prototype proto = BinaryChunk.undump(data);
        luaMain(proto);
    }

    private static void luaMain(Prototype proto) {
        LuaVM vm = new LuaStateImpl(proto);
        vm.setTop(proto.getMaxStackSize());
        for (;;) {
            int pc = vm.getPC();
            int i = vm.fetch();
            OpCode opCode = Instruction.getOpCode(i);
            if (opCode != OpCode.RETURN) {
                opCode.getAction().execute(i, vm);

                System.out.printf("[%02d] %-8s ", pc+1, opCode.name());
                printStack(vm);
            } else {
                break;
            }
        }
    }

    private static void printStack(LuaState ls) {
        int top = ls.getTop();
        for (int i = 1; i <= top; i++) {
            LuaType t = ls.type(i);
            switch (t) {
                case LUA_TBOOLEAN:
                    System.out.printf("[%b]", ls.toBoolean(i));
                    break;
                case LUA_TNUMBER:
                    if (ls.isInteger(i)) {
                        System.out.printf("[%d]", ls.toInteger(i));
                    } else {
                        System.out.printf("[%f]", ls.toNumber(i));
                    }
                    break;
                case LUA_TSTRING:
                    System.out.printf("[\"%s\"]", ls.toString(i));
                    break;
                default: // other values
                    System.out.printf("[%s]", ls.typeName(t));
                    break;
            }
        }
        System.out.println();
    }
}
