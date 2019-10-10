package state;

import binchunk.Prototype;
import lombok.Getter;

@Getter
public class Closure {

    final Prototype proto;

    Closure(Prototype proto) {
        this.proto = proto;
    }
}
