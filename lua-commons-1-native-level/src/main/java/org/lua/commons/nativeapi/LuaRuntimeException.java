package org.lua.commons.nativeapi;

public class LuaRuntimeException extends RuntimeException {
    public LuaRuntimeException(String message) {
        super(message);
    }

    public LuaRuntimeException(String message, Exception cause) {
        super(message, cause);
    }
}
