package binchunk;

import java.nio.ByteBuffer;

public class LocVar {

    private String varName;
    private int startPC;
    private int endPC;

    void read(ByteBuffer buf) {
        varName = BinaryChunk.getLuaString(buf);
        startPC = buf.getInt();
        endPC = buf.getInt();
    }
}
