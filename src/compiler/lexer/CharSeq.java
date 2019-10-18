package compiler.lexer;

public class CharSeq implements CharSequence {

    private String str;
    private int pos;

    CharSeq(String str) {
        this.str = str;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int i) {
        return 0;
    }

    @Override
    public CharSequence subSequence(int i, int i1) {
        return null;
    }
}
