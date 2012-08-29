package org.lua.commons.customapi.container;

import org.lua.commons.baseapi.LuaThread;

public interface LuaLibrary {

	public String getNamespace();

	public String[] getNames();

	public void prepare(LuaThread thread);

}
