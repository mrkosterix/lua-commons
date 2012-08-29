package org.lua.commons.nativeapi;

public class LuaStateApiProvider {

    private static LuaStateApiFactory stateFactory = null;

    public static boolean hasFactory() {
        return stateFactory != null;
    }

    public static LuaStateApiFactory getFactory() {
        if (stateFactory == null)
            throw new LuaRuntimeException("LuaStateApiFactory not set");
        return stateFactory;
    }

    public static void setFactory(LuaStateApiFactory factory) {
        stateFactory = factory;
    }
}
