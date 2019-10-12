package state;

import api.LuaType;
import number.LuaNumber;

import static api.LuaType.*;

public class LuaValue {

    static LuaType typeOf(Object val) {
        if (val == null) {
            return LUA_TNIL;
        } else if (val instanceof Boolean) {
            return LUA_TBOOLEAN;
        } else if (val instanceof Long || val instanceof Double) {
            return LUA_TNUMBER;
        } else if (val instanceof String) {
            return LUA_TSTRING;
        } else if (val instanceof LuaTable) {
            return LUA_TTABLE;
        } else if (val instanceof Closure) {
            return LUA_TFUNCTION;
        } else {
            throw new RuntimeException("TODO");
        }
    }

    static boolean toBoolean(Object val) {
        if (val == null) {
            return false;
        } else if (val instanceof Boolean) {
            return (Boolean) val;
        } else {
            return true;
        }
    }

    static Double toFloat(Object val) {
        if (val instanceof Double) {
            return (Double) val;
        } else if (val instanceof Long) {
            return ((Long) val).doubleValue();
        } else if (val instanceof String) {
            return LuaNumber.parseFloat((String) val);
        } else {
            return null;
        }
    }

    static Long toInteger(Object val) {
        if (val instanceof Long) {
            return (Long) val;
        } else if (val instanceof Double) {
            double n = (Double) val;
            return LuaNumber.isInteger(n) ? (long) n : null;
        } else if (val instanceof String) {
            return toInteger((String) val);
        } else {
            return null;
        }
    }

    private static Long toInteger(String s) {
        Long i = LuaNumber.parseInteger(s);
        if (i != null) {
            return i;
        }
        Double f = LuaNumber.parseFloat(s);
        if (f != null && LuaNumber.isInteger(f)) {
            return f.longValue();
        }
        return null;
    }
}
