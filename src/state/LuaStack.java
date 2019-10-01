package state;

import java.util.ArrayList;
import java.util.Collections;

/*
    The Lua stack as a bridge between the host language and the Lua language
 */
public class LuaStack {

    private final ArrayList<Object> slots = new ArrayList<>();

    int top() {
        return slots.size();
    }

    void push(Object value) {
        //TODO There should be a better way to address stack size
        if (slots.size() > 10000) {
            throw new StackOverflowError();
        }
        slots.add(value);
    }

    Object pop() {
        return slots.remove(slots.size() - 1);
    }

    int absIndex(int idx) {
        return idx >= 0 ? idx : idx + slots.size() + 1;
    }

    boolean isValid(int idx) {
        int asbIdx = absIndex(idx);
        return asbIdx > 0 && asbIdx <= slots.size();
    }

    Object get(int idx) {
        int absIdx = absIndex(idx);
        // The index in the lua stack starts at 1
        if (absIdx > 0 && absIdx <= slots.size()) {
            return slots.get(absIdx - 1);
        } else {
            return null;
        }
    }

    void set(int idx, Object value) {
        int absIdx = absIndex(idx);
        slots.set(absIdx - 1, value);
    }

    void reverse(int from, int to) {
        Collections.reverse(slots.subList(from, to + 1));
    }
}
