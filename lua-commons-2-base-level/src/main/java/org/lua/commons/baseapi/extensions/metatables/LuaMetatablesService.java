package org.lua.commons.baseapi.extensions.metatables;

import org.lua.commons.baseapi.extensions.LuaExtension;
import org.lua.commons.baseapi.types.LuaMetatable;

public interface LuaMetatablesService extends LuaExtension {
	
	public boolean isRegistered(Class<?> key);

	public void register(Class<?> key, LuaMetatable metatable);

	public void unregister(Class<?> key);

	public LuaMetatable get(Object obj);

}
