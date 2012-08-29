package org.lua.commons.webapi.json;

import org.lua.commons.nativeapi.LuaRuntimeException;

public class LuaJsonCastException extends LuaRuntimeException {

	public LuaJsonCastException(String message) {
		super(message);
	}

	public LuaJsonCastException(String message, Exception cause) {
		super(message, cause);
	}

}
