package org.lua.commons.webapi;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.customapi.container.LuaContainer;

public interface LuaFrame {

	public LuaTable prepare(Lua lua);

	public LuaContainer getContainer();

}
