package state;

import api.JavaFunction;
import binchunk.Prototype;
import lombok.Getter;

@Getter
public class Closure {

    final Prototype proto;
    final JavaFunction javaFunc;

    // Lua closure
    Closure(Prototype proto) {
        this.proto = proto;
        this.javaFunc = null;
    }

    // java closure
    Closure(JavaFunction javaFunc) {
        this.proto = null;
        this.javaFunc = javaFunc;
    }
}
