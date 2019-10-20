package compiler.codegen;

import vm.OpCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static vm.Instruction.MAXARG_sBx;

public class FuncInfo {

    static class UpvalInfo {
        int locVarSlot;
        int upvalIndex;
        int index;
    }

    static class LocVarInfo {
        LocVarInfo prev;
        String name;
        int scopeLv;
        int slot;
        int startPC;
        int endPC;
        boolean captured;
    }

    private FuncInfo parent;
    Map<Object, Integer> constants = new HashMap<>();
    int usedRegs;
    int maxRegs;
    private int scopeLv;
    List<LocVarInfo> locVars = new ArrayList<>();
    private Map<String, LocVarInfo> locNames = new HashMap<>();
    private List<List<Integer>> breaks = new ArrayList<>();
    Map<String, UpvalInfo> upvalues = new HashMap<>();
    List<Integer> insts = new ArrayList<>();
    List<Integer> lineNums = new ArrayList<>();

    /* constants */

    int indexOfConstant(Object k) {
        Integer idx = constants.get(k);
        if (idx != null) {
            return idx;
        }

        idx = constants.size();
        constants.put(k, idx);
        return idx;
    }

    /* registers */

    int allocReg() {
        usedRegs++;
        if (usedRegs >= 255) {
            throw new RuntimeException("function or expression needs too many registers");
        }
        if (usedRegs > maxRegs) {
            maxRegs = usedRegs;
        }
        return usedRegs - 1;
    }

    void freeReg() {
        if (usedRegs <= 0) {
            throw new RuntimeException("usedRegs <= 0 !");
        }
        usedRegs--;
    }

    int allocRegs(int n) {
        if (n <= 0) {
            throw new RuntimeException("n <= 0 !");
        }
        for (int i = 0; i < n; i++) {
            allocReg();
        }
        return usedRegs - n;
    }

    void freeRegs(int n) {
        if (n < 0) {
            throw new RuntimeException("n < 0 !");
        }
        for (int i = 0; i < n; i++) {
            freeReg();
        }
    }

    /* lexical scope */

    void enterScope(boolean breakable) {
        scopeLv++;
        if (breakable) {
            breaks.add(new ArrayList<>());
        } else {
            breaks.add(null);
        }
    }

    void exitScope(int endPC) {

    }

    int addLocVar(String name, int startPC) {
        LocVarInfo newVar = new LocVarInfo();
        newVar.name = name;
        newVar.prev = locNames.get(name);
        newVar.scopeLv = scopeLv;
        newVar.slot = allocReg();
        newVar.startPC = startPC;
        newVar.endPC = 0;

        locVars.add(newVar);
        locNames.put(name, newVar);

        return newVar.slot;
    }

    private void removeLocVar(LocVarInfo locVar) {
        freeReg();
        if (locVar.prev == null) {
            locNames.remove(locVar.name);
        } else if (locVar.prev.scopeLv == locVar.scopeLv) {
            removeLocVar(locVar.prev);
        } else {
            locNames.put(locVar.name, locVar.prev);
        }
    }

    int slotOfLocVar(String name) {
        return locNames.containsKey(name)
                ? locNames.get(name).slot
                : -1;
    }

    void addBreakJmp(int pc) {
        for (int i = scopeLv; i >= 0; i--) {
            if (breaks.get(i) != null) {
                breaks.get(i).add(pc);
                return;
            }
        }

        throw new RuntimeException("<break> at line ? not inside a loop!");
    }

    /* upvalues */

    int indexOfUpval(String name) {
        if (upvalues.containsKey(name)) {
            return upvalues.get(name).index;
        }
        if (parent != null) {
            if (parent.locNames.containsKey(name)) {
                LocVarInfo locVar = parent.locNames.get(name);
                int idx = upvalues.size();
                UpvalInfo upval = new UpvalInfo();
                upval.locVarSlot = locVar.slot;
                upval.upvalIndex = -1;
                upval.index = idx;
                upvalues.put(name, upval);
                locVar.captured = true;
                return idx;
            }
            int uvIdx = parent.indexOfUpval(name);
            if (uvIdx >= 0) {
                int idx = upvalues.size();
                UpvalInfo upval = new UpvalInfo();
                upval.locVarSlot = -1;
                upval.upvalIndex = uvIdx;
                upval.index = idx;
                upvalues.put(name, upval);
                return idx;
            }
        }
        return -1;
    }

    /* code */

    int pc() {
        return insts.size() - 1;
    }

    void fixSbx(int pc, int sBx) {
        int i = insts.get(pc);
        i = i << 18 >> 18;
        i = i | (sBx + MAXARG_sBx) << 14;
        insts.set(pc, i);
    }

    void emitABC(int line, OpCode opcode, int a, int b, int c) {
        int i = b << 23 | c << 14 | a << 6 | opcode.ordinal();
        insts.add(i);
        lineNums.add(line);
    }

    private void emitABx(int line, OpCode opcode, int a, int bx) {
        int i = bx << 14 | a << 6 | opcode.ordinal();
        insts.add(i);
        lineNums.add(line);
    }

    private void emitAsBx(int line, OpCode opcode, int a, int sBx) {
        int i = (sBx + MAXARG_sBx) << 14 | a << 6 | opcode.ordinal();
        insts.add(i);
        lineNums.add(line);
    }

    private void emitAx(int line, OpCode opcode, int ax) {
        int i = ax<<6 | opcode.ordinal();
        insts.add(i);
        lineNums.add(line);
    }
}
