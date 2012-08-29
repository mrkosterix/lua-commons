package org.lua.commons.configuration;

import org.lua.commons.nativeapi.LuaRuntimeException;

public class LuaConfigurationException extends LuaRuntimeException {

	public LuaConfigurationException(String message, Exception cause) {
		super(message, cause);
	}

	public LuaConfigurationException(String message) {
		super(message);
	}

}
