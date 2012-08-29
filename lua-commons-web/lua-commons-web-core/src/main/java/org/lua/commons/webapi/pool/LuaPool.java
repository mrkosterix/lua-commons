package org.lua.commons.webapi.pool;

public interface LuaPool {

	public LuaEntry get();

	public void free(LuaEntry lua);

}
