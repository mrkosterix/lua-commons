package org.lua.commons.webapi.pool;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.types.LuaTable;

public class LuaEntry {

	private final Lua lua;

	private final LuaTable env;

	public LuaEntry(Lua lua, LuaTable env) {
		this.lua = lua;
		this.env = env;
	}

	public Lua getLua() {
		return lua;
	}

	public LuaTable getEnv() {
		return env;
	}

}
