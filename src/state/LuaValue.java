package state;

import api.LuaType;

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
        } else {
            throw new RuntimeException("TYPEOF TODO");
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
}
