package binchunk;

public class Prototype {

    private String source;
    private int lineDefined;
    private int lastLineDefined;
    private byte numParams;
    private byte isVararg;
    private byte maxStackSize;
    private int[] code;
    private Object[] constants;
    private Upvalue[] upvalues;
    private Prototype[] protos;
    private int[] lineInfo;
    private LocVar[] locVars;
    private String[] upvalueNames;
}
