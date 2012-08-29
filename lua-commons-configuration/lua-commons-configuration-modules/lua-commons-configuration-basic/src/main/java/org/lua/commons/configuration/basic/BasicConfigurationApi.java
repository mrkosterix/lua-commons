package org.lua.commons.configuration.basic;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.customapi.container.LuaLibrary;

public class BasicConfigurationApi implements LuaLibrary {

	public String[] getNames() {
		return new String[] { "error", "next", "tonumber", "tostring", "type" };
	}

	public String getNamespace() {
		return "";
	}

	public void prepare(LuaThread thread) {
		// do nothing
	}

}
