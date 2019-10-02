import binchunk.BinaryChunk;
import binchunk.LocVar;
import binchunk.Prototype;
import binchunk.Upvalue;
import static vm.Instruction.*;
import static vm.OpArgMask.*;
import vm.OpCode;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DecompilingMain {
    public static void main(String[] args) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get("test/resources/hw.luac"));
        Prototype proto = BinaryChunk.undump(data);
        list(proto);
    }

    private static void list(Prototype func) {
        printHeader(func);
        printCode(func);
        printDetail(func);
        for (Prototype p : func.getProtos()) {
            list(p);
        }
    }

    private static void printHeader(Prototype f) {
        String funcType = f.getLineDefined() > 0 ? "function" : "main";
        String varargFlag = f.getIsVararg() > 0 ? "+" : "";

        System.out.printf("\n%s <%s:%d,%d> (%d instructions)\n",
                funcType, f.getSource(), f.getLineDefined(), f.getLastLineDefined(),
                f.getCode().length);

        System.out.printf("%d%s params, %d slots, %d upvalues, ",
                f.getNumParams(), varargFlag, f.getMaxStackSize(), f.getUpvalues().length);

        System.out.printf("%d locals, %d constants, %d functions\n",
                f.getLocVars().length, f.getConstants().length, f.getProtos().length);
    }

    private static void printCode(Prototype f) {
        int[] code = f.getCode();
        int[] lineInfo = f.getLineInfo();
        for (int i = 0; i < code.length; i++) {
            String line = lineInfo.length > 0 ? String.valueOf(lineInfo[i]) : "-";
            System.out.printf("\t%d\t[%s]\t%-8s \t", i + 1, line, getOpCode(code[i]));
            printOperands(code[i]);
            System.out.println();
        }
    }

    private static void printOperands(int i) {
        OpCode opCode = getOpCode(i);
        int a = getA(i);
        switch (opCode.getOpMode()) {
            case iABC:
                System.out.printf("%d", a);
                if (opCode.getArgBMode() != OpArgN) {
                    int b = getB(i);
                    System.out.printf(" %d", b > 0xFF ? -1 - (b & 0xFF) : b);
                }
                if (opCode.getArgCMode() != OpArgN) {
                    int c = getC(i);
                    System.out.printf(" %d", c > 0xFF ? -1 - (c & 0xFF) : c);
                }
                break;
            case iABx:
                System.out.printf("%d", a);
                int bx = getBx(i);
                if (opCode.getArgBMode() == OpArgK) {
                    System.out.printf(" %d", -1 - bx);
                } else if (opCode.getArgBMode() == OpArgU) {
                    System.out.printf(" %d", bx);
                }
                break;
            case iAsBx:
                int sbx = getSBx(i);
                System.out.printf("%d %d", a, sbx);
                break;
            case iAx:
                int ax = getAx(i);
                System.out.printf("%d", -1 - ax);
                break;
        }
    }

    private static void printDetail(Prototype f) {
        System.out.printf("constants (%d):\n", f.getConstants().length);
        int i = 1;
        for (Object k : f.getConstants()) {
            System.out.printf("\t%d\t%s\n", i++, constantToString(k));
        }

        i = 0;
        System.out.printf("locals (%d):\n", f.getLocVars().length);
        for (LocVar locVar : f.getLocVars()) {
            System.out.printf("\t%d\t%s\t%d\t%d\n", i++,
                    locVar.getVarName(), locVar.getStartPC() + 1, locVar.getEndPC() + 1);
        }

        i = 0;
        System.out.printf("upvalues (%d):\n", f.getUpvalues().length);
        for (Upvalue upval : f.getUpvalues()) {
            String name = f.getUpvalueNames().length > 0 ? f.getUpvalueNames()[i] : "-";
            System.out.printf("\t%d\t%s\t%d\t%d\n", i++,
                    name, upval.getInstack(), upval.getIdx());
        }
    }

    private static String constantToString(Object k) {
        if (k == null) {
            return "nil";
        } else if (k instanceof String) {
            return "\"" + k + "\"";
        } else {
            return k.toString();
        }
    }

}
