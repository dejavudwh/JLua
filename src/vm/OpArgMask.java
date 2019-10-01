package vm;

/*
    指令操作数类型
 */
public enum OpArgMask {

    OpArgN, // argument is not used
    OpArgU, // argument is used
    OpArgR, // argument is a register(iABC) or a jump offset(iAsBx)
    OpArgK, // argument is a constant(iABx) or register/constant(iABC)
    ;
}
