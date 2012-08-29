package org.lua.commons.nativeapi;

public class LuaException extends Exception {
    public LuaException(String message) {
        super(message);
    }

    public LuaException(String message, Exception cause) {
        super(message, cause);
    }
}
