package org.lua.commons.webapi.json;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.extensions.LuaExtension;
import org.lua.commons.baseapi.types.LuaObject;

public interface JsonTypeCastManager extends LuaExtension {
	public String toJson(LuaThread thread, LuaObject obj);

	public LuaObject toLua(LuaThread thread, String json);
}
