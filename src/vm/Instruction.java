package vm;

public class Instruction {

    /*
        iABC  |   B:9   |   c:9   |    A:8    | opcode:6 |
        iABx  |        Bx:18      |    A:8    | opcode:6 |
        iAsBx |       sBx:18      |    A:8    | opcode:6 |
        iAx   |              Ax:26            | opcode:6 |
     */
    public static final int MAXARG_Bx = (1 << 18) - 1;// sBx 18 bit; 2^18 - 1
    public static final int MAXARG_sBx = MAXARG_Bx >> 1;//MAXARG_Bx / 2

    public static OpCode getOpCode(int i) {
        // low 6 bit
        return OpCode.values()[i & 0x3F];
    }

    public static int getA(int i) {
        // >> 6 low 8 bit
        return (i >> 6) & 0xFF;
    }

    public static int getC(int i) {
        // >> 14 low 9 bit
        return (i >> 14) & 0x1FF;
    }

    public static int getB(int i) {
        return (i >> 23) & 0x1FF;
    }

    public static int getBx(int i) {
        return (i >>> 14);
    }

    public static int getSBx(int i) {
        return getBx(i) - MAXARG_sBx;
    }

    public static int getAx(int i) {
        return i >>> 6;
    }
}
