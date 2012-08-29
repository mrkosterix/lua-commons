package org.lua.commons.customapi.container;

import org.lua.commons.nativeapi.LuaRuntimeException;

public class LuaLibraryException extends LuaRuntimeException {

	public LuaLibraryException(String message, Exception cause) {
		super(message, cause);
	}

	public LuaLibraryException(String message) {
		super(message);
	}

}
