package state;

import api.JavaFunction;
import binchunk.Prototype;
import lombok.Getter;

@Getter
public class Closure {

    final Prototype proto;
    final JavaFunction javaFunc;
    final UpvalueHolder[] upvals;

    // Lua closure
    Closure(Prototype proto) {
        this.proto = proto;
        this.javaFunc = null;
        this.upvals = new UpvalueHolder[proto.getUpvalues().length];
    }

    // java closure
    Closure(JavaFunction javaFunc, int nUpvals) {
        this.proto = null;
        this.javaFunc = javaFunc;
        this.upvals = new UpvalueHolder[nUpvals];
    }
}
