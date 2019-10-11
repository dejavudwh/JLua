package api;

@FunctionalInterface
public interface JavaFunction {

    int invoke(LuaState ls);
}

