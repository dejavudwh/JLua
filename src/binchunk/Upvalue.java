package binchunk;

import java.nio.ByteBuffer;

public class Upvalue {

    private byte instack;
    private byte idx;

    void read(ByteBuffer buf) {
        instack = buf.get();
        idx = buf.get();
    }
}
