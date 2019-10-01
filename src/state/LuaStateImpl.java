package state;

import api.LuaState;
import api.LuaType;

/*
    Lua interpreter state
 */
public class LuaStateImpl implements LuaState {

    private LuaStack stack = new LuaStack();

    @Override
    public int getTop() {
        return stack.top();
    }

    @Override
    public int absIndex(int idx) {
        return stack.absIndex(idx);
    }

    @Override
    public boolean checkStack(int n) {
        //TODO Check whether n values can be pushed in, if not, try to expand capacity
        return true;
    }

    @Override
    public void pop(int n) {
        for (int i = 0; i < n; i++) {
            stack.pop();
        }
    }

    @Override
    public void copy(int fromIdx, int toIdx) {
        stack.set(toIdx, stack.get(fromIdx));
    }

    @Override
    public void pushValue(int idx) {
        stack.push(stack.get(idx));
    }

    @Override
    public void replace(int idx) {
        stack.set(idx, stack.pop());
    }

    @Override
    public void insert(int idx) {

    }

    @Override
    public void remove(int idx) {

    }

    @Override
    public void rotate(int idx, int n) {
        int t = stack.top() - 1;
        int p = stack.absIndex(idx) - 1;
        int m = n >= 0 ? t - n : p - n - 1;

        /*
            ⬆   ⬆
            |   |
            ⬇   |
            ⬆   |
            |   |
            ⬇   ⬇
         */
        stack.reverse(p, m);
        stack.reverse(m + 1, t);
        stack.reverse(p, t);
    }

    @Override
    public void setTop(int idx) {

    }

    @Override
    public String typeName(LuaType tp) {
        return null;
    }

    @Override
    public LuaType type(int idx) {
        return null;
    }

    @Override
    public boolean isNone(int idx) {
        return false;
    }

    @Override
    public boolean isNil(int idx) {
        return false;
    }

    @Override
    public boolean isNoneOrNil(int idx) {
        return false;
    }

    @Override
    public boolean isBoolean(int idx) {
        return false;
    }

    @Override
    public boolean isInteger(int idx) {
        return false;
    }

    @Override
    public boolean isNumber(int idx) {
        return false;
    }

    @Override
    public boolean isString(int idx) {
        return false;
    }

    @Override
    public boolean isTable(int idx) {
        return false;
    }

    @Override
    public boolean isThread(int idx) {
        return false;
    }

    @Override
    public boolean isFunction(int idx) {
        return false;
    }

    @Override
    public boolean toBoolean(int idx) {
        return false;
    }

    @Override
    public long toInteger(int idx) {
        return 0;
    }

    @Override
    public Long toIntegerX(int idx) {
        return null;
    }

    @Override
    public double toNumber(int idx) {
        return 0;
    }

    @Override
    public Double toNumberX(int idx) {
        return null;
    }

    @Override
    public String toString(int idx) {
        return null;
    }

    @Override
    public void pushNil() {

    }

    @Override
    public void pushBoolean(boolean b) {

    }

    @Override
    public void pushInteger(long n) {

    }

    @Override
    public void pushNumber(double n) {

    }

    @Override
    public void pushString(String s) {

    }
}
