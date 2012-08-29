package org.lua.commons.customapi.javafunctions.handlers;

import org.lua.commons.nativeapi.LuaRuntimeException;

public class LuaTypeCastException extends LuaRuntimeException {

	public LuaTypeCastException(String message) {
		super(message);
	}

	public LuaTypeCastException(String message, Exception cause) {
		super(message, cause);
	}

}
