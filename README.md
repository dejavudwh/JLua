# JLua

![](https://img.shields.io/badge/language-Java-blue.svg)
![](https://img.shields.io/badge/category-learning-yellow.svg)
[![](https://img.shields.io/badge/blog-@dejavudwh-red.svg)](https://dejavudwh.cn/)
![](http://progressed.io/bar/82?title=done)

> Lua compiler and Lua virtual machine implemented in Java. [Repo code source](https://github.com/zxh0/luago-book/tree/master/code/go)


## Get start

First read the Lua source file, then register the standard library function, and finally load the Lua source file to call the main function.

```java
public class Test {

    public static void main(String[] args) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get("test/resources/test.luac"));
        LuaState ls = new LuaStateImpl();
        ls.register("print", BasicLib::print);
        ls.register("getmetatable", BasicLib::getMetatable);
        ls.register("setmetatable", BasicLib::setMetatable);
        ls.register("next", BasicLib::next);
        ls.register("pairs", BasicLib::pairs);
        ls.register("ipairs", BasicLib::iPairs);
        ls.load(data, "test.luac", "b");
        ls.call(0, 0);
    }
}
```

## Code structure

Four of the most important part is: binchunk, compiler, the state, vm

### binchunk package

> Parse the compiled binary chunk and convert it into Java's internal data structure to execute the instructions of the virtual machine.

```java
public static Prototype undump(byte[] data) {
        ByteBuffer buf = ByteBuffer.wrap(data)
                .order(ByteOrder.LITTLE_ENDIAN);
        checkHead(buf);
        buf.get(); // size_upvalues
        Prototype mainFunc = new Prototype();
        mainFunc.read(buf, "");
        return mainFunc;
    }

// Prototype
void read(ByteBuffer buf, String parentSource) {
        source = BinaryChunk.getLuaString(buf);
        if (source.isEmpty()) {
            source = parentSource;
        }
        lineDefined = buf.getInt();
        lastLineDefined = buf.getInt();
        numParams = buf.get();
        isVararg = buf.get();
        maxStackSize = buf.get();
        readCode(buf);
        readConstants(buf);
        readUpvalues(buf);
        readProtos(buf, source);
        readLineInfo(buf);
        readLocVars(buf);
        readUpvalueNames(buf);
    }
```

The entire Lua file can be thought of as a huge main function, which wraps other functions, and the binary chunk corresponds to the main function, which contains constant table information, instructions, upvalues information, sub-function information, etc.


### state package

> LuaState is the core part of it. Lua's virtual is a register-based virtual machine, but the internal implementation of the register uses the stack. LuaState provides operations on the stack and operations on the data structures provided by Lua implemented in Java. To implement the instruction set

```java
class LuaStack {

    /* virtual stack */
    private final ArrayList<Object> slots = new ArrayList<>();
    /* call info */
    LuaStateImpl state;
    Closure closure;
    List<Object> varargs;
    Map<Integer, UpvalueHolder> openuvs;
    int pc;
    /* linked list */
    LuaStack prev;
}
```

The basic properties of the stack inside LuaState, the basic stack operation will use it, where the function call will form a chained call stack


### vm package

> The vm package describes the format of the Lua instruction, the implementation of the instruction set, etc. The Instructions, which are based on the API provided by LuaState, implement Lua's instruction set.

```java
public enum OpCode {

    /*       T  A    B       C     mode */
    MOVE    (0, 1, OpArgR, OpArgN, iABC , Instructions::move    ), // R(A) := R(B)
    LOADK   (0, 1, OpArgK, OpArgN, iABx , Instructions::loadK   ), // R(A) := Kst(Bx)
    LOADKX  (0, 1, OpArgN, OpArgN, iABx , Instructions::loadKx  ), // R(A) := Kst(extra arg)
    LOADBOOL(0, 1, OpArgU, OpArgU, iABC , Instructions::loadBool), // R(A) := (bool)B; if (C) pc++
    LOADNIL (0, 1, OpArgU, OpArgN, iABC , Instructions::loadNil ), // R(A), R(A+1), ..., R(A+B) := nil
    GETUPVAL(0, 1, OpArgU, OpArgN, iABC , Instructions::getUpval), // R(A) := UpValue[B]
    GETTABUP(0, 1, OpArgU, OpArgK, iABC , Instructions::getTabUp), // R(A) := UpValue[B][RK(C)]
    GETTABLE(0, 1, OpArgR, OpArgK, iABC , Instructions::getTable), // R(A) := R(B)[RK(C)]
    SETTABUP(0, 0, OpArgK, OpArgK, iABC , Instructions::setTabUp), // UpValue[A][RK(B)] := RK(C)
    SETUPVAL(0, 0, OpArgU, OpArgN, iABC , Instructions::setUpval), // UpValue[B] := R(A)
    SETTABLE(0, 0, OpArgK, OpArgK, iABC , Instructions::setTable), // R(A)[RK(B)] := RK(C)
    NEWTABLE(0, 1, OpArgU, OpArgU, iABC , Instructions::newTable), // R(A) := {} (size = B,C)
    SELF    (0, 1, OpArgR, OpArgK, iABC , Instructions::self    ), // R(A+1) := R(B); R(A) := R(B)[RK(C)]
    ADD     (0, 1, OpArgK, OpArgK, iABC , Instructions::add     ), // R(A) := RK(B) + RK(C)
    SUB     (0, 1, OpArgK, OpArgK, iABC , Instructions::sub     ), // R(A) := RK(B) - RK(C)
    MUL     (0, 1, OpArgK, OpArgK, iABC , Instructions::mul     ), // R(A) := RK(B) * RK(C)
    MOD     (0, 1, OpArgK, OpArgK, iABC , Instructions::mod     ), // R(A) := RK(B) % RK(C)
    POW     (0, 1, OpArgK, OpArgK, iABC , Instructions::pow     ), // R(A) := RK(B) ^ RK(C)
    DIV     (0, 1, OpArgK, OpArgK, iABC , Instructions::div     ), // R(A) := RK(B) / RK(C)
    IDIV    (0, 1, OpArgK, OpArgK, iABC , Instructions::idiv    ), // R(A) := RK(B) // RK(C)
    BAND    (0, 1, OpArgK, OpArgK, iABC , Instructions::band    ), // R(A) := RK(B) & RK(C)
    BOR     (0, 1, OpArgK, OpArgK, iABC , Instructions::bor     ), // R(A) := RK(B) | RK(C)
    BXOR    (0, 1, OpArgK, OpArgK, iABC , Instructions::bxor    ), // R(A) := RK(B) ~ RK(C)
    SHL     (0, 1, OpArgK, OpArgK, iABC , Instructions::shl     ), // R(A) := RK(B) << RK(C)
    SHR     (0, 1, OpArgK, OpArgK, iABC , Instructions::shr     ), // R(A) := RK(B) >> RK(C)
    UNM     (0, 1, OpArgR, OpArgN, iABC , Instructions::unm     ), // R(A) := -R(B)
    BNOT    (0, 1, OpArgR, OpArgN, iABC , Instructions::bnot    ), // R(A) := ~R(B)
    NOT     (0, 1, OpArgR, OpArgN, iABC , Instructions::not     ), // R(A) := not R(B)
    LEN     (0, 1, OpArgR, OpArgN, iABC , Instructions::length  ), // R(A) := length of R(B)
    CONCAT  (0, 1, OpArgR, OpArgR, iABC , Instructions::concat  ), // R(A) := R(B).. ... ..R(C)
    JMP     (0, 0, OpArgR, OpArgN, iAsBx, Instructions::jmp     ), // pc+=sBx; if (A) close all upvalues >= R(A - 1)
    EQ      (1, 0, OpArgK, OpArgK, iABC , Instructions::eq      ), // if ((RK(B) == RK(C)) ~= A) then pc++
    LT      (1, 0, OpArgK, OpArgK, iABC , Instructions::lt      ), // if ((RK(B) <  RK(C)) ~= A) then pc++
    LE      (1, 0, OpArgK, OpArgK, iABC , Instructions::le      ), // if ((RK(B) <= RK(C)) ~= A) then pc++
    TEST    (1, 0, OpArgN, OpArgU, iABC , Instructions::test    ), // if not (R(A) <=> C) then pc++
    TESTSET (1, 1, OpArgR, OpArgU, iABC , Instructions::testSet ), // if (R(B) <=> C) then R(A) := R(B) else pc++
    CALL    (0, 1, OpArgU, OpArgU, iABC , Instructions::call    ), // R(A), ... ,R(A+C-2) := R(A)(R(A+1), ... ,R(A+B-1))
    TAILCALL(0, 1, OpArgU, OpArgU, iABC , Instructions::tailCall), // return R(A)(R(A+1), ... ,R(A+B-1))
    RETURN  (0, 0, OpArgU, OpArgN, iABC , Instructions::_return ), // return R(A), ... ,R(A+B-2)
    FORLOOP (0, 1, OpArgR, OpArgN, iAsBx, Instructions::forLoop ), // R(A)+=R(A+2); if R(A) <?= R(A+1) then { pc+=sBx; R(A+3)=R(A) }
    FORPREP (0, 1, OpArgR, OpArgN, iAsBx, Instructions::forPrep ), // R(A)-=R(A+2); pc+=sBx
    TFORCALL(0, 0, OpArgN, OpArgU, iABC , Instructions::tForCall), // R(A+3), ... ,R(A+2+C) := R(A)(R(A+1), R(A+2));
    TFORLOOP(0, 1, OpArgR, OpArgN, iAsBx, Instructions::tForLoop), // if R(A+1) ~= nil then { R(A)=R(A+1); pc += sBx }
    SETLIST (0, 0, OpArgU, OpArgU, iABC , Instructions::setList ), // R(A)[(C-1)*FPF+i] := R(A+i), 1 <= i <= B
    CLOSURE (0, 1, OpArgU, OpArgN, iABx , Instructions::closure ), // R(A) := closure(KPROTO[Bx])
    VARARG  (0, 1, OpArgU, OpArgN, iABC , Instructions::vararg  ), // R(A), R(A+1), ..., R(A+B-2) = vararg
    EXTRAARG(0, 0, OpArgU, OpArgU, iAx  , null                  ), // extra (larger) argument for previous opcode
    ;

}
```


### compiler package

> The compiler package is a compiler implementation for Lua. The parsing uses a top-down parsing method to generate the AST. The code generation part will generate a Prototype-like data structure, and finally convert it to Prototype and load it into the virtual machine.


```java
class FuncInfo {

    private static final Map<TokenKind, OpCode> arithAndBitwiseBinops = new HashMap<>();
    static {
        arithAndBitwiseBinops.put(TOKEN_OP_ADD,  OpCode.ADD);
        arithAndBitwiseBinops.put(TOKEN_OP_SUB,  OpCode.SUB);
        arithAndBitwiseBinops.put(TOKEN_OP_MUL,  OpCode.MUL);
        arithAndBitwiseBinops.put(TOKEN_OP_MOD,  OpCode.MOD);
        arithAndBitwiseBinops.put(TOKEN_OP_POW,  OpCode.POW);
        arithAndBitwiseBinops.put(TOKEN_OP_DIV,  OpCode.DIV);
        arithAndBitwiseBinops.put(TOKEN_OP_IDIV, OpCode.IDIV);
        arithAndBitwiseBinops.put(TOKEN_OP_BAND, OpCode.BAND);
        arithAndBitwiseBinops.put(TOKEN_OP_BOR,  OpCode.BOR);
        arithAndBitwiseBinops.put(TOKEN_OP_BXOR, OpCode.BXOR);
        arithAndBitwiseBinops.put(TOKEN_OP_SHL,  OpCode.SHL);
        arithAndBitwiseBinops.put(TOKEN_OP_SHR,  OpCode.SHR);
    }

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
    List<FuncInfo> subFuncs = new ArrayList<>();
    int usedRegs;
    int maxRegs;
    private int scopeLv;
    List<LocVarInfo> locVars = new ArrayList<>();
    private Map<String, LocVarInfo> locNames = new HashMap<>();
    Map<String, UpvalInfo> upvalues = new HashMap<>();
    Map<Object, Integer> constants = new HashMap<>();
    private List<List<Integer>> breaks = new ArrayList<>();
    List<Integer> insts = new ArrayList<>();
    List<Integer> lineNums = new ArrayList<>();
    int line;
    int lastLine;
    int numParams;
    boolean isVararg;
}
```