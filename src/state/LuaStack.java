package state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
    The Lua stack as a bridge between the host language and the Lua language
 */
public class LuaStack {

    /* Virtual machine support */
    private final ArrayList<Object> slots = new ArrayList<>();

    /* Function call support */
    Closure closure;
    List<Object> varargs;
    int pc;

    /* linked list for implementation of the call stack with linked list */
    LuaStack prev;


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

    /* Implement function calls to newly added functions */

    void pushN(List<Object> vals, int n) {
        int nVals = vals == null ? 0 : vals.size();
        if (n < 0) {
            n = nVals;
        }
        for (int i = 0; i < n; i++) {
            push(i < nVals ? vals.get(i) : null);
        }
    }

    List<Object> popN(int n) {
        List<Object> vals = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            vals.add(pop());
        }
        Collections.reverse(vals);
        return vals;
    }
}
