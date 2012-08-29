package org.lua.commons.customapi.sandbox;

import org.lua.commons.nativeapi.LuaRuntimeException;

public class LuaOperationPermissionDenied extends LuaRuntimeException {
	public LuaOperationPermissionDenied(String message) {
		super(message);
	}

	public LuaOperationPermissionDenied(String message, Exception cause) {
		super(message, cause);
	}
}
