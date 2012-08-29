package org.lua.commons.customapi.javafunctions.handlers;

import static org.lua.commons.baseapi.types.LuaObjectTools.fromlua;
import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;
import static org.lua.commons.customapi.javafunctions.handlers.CastUtils.totype;

import java.util.Map;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.customapi.javafunctions.handlers.types.Type;

public class DefaultTypeHandler extends TypeHandler {

	public DefaultTypeHandler(TypeCastManager castManager) {
		super(castManager);
	}

	@Override
	public Class<?>[] getClasses() {
		return null;
	}

	@Override
	public Object handleFrom(LuaThread thread, LuaObject obj, Type expected) {
		if (obj instanceof LuaTable) {
			return castManager.castFrom(thread, obj,
					totype(Map.class, Object.class, Object.class));
		}
		return fromlua(thread, obj);
	}

	@Override
	public LuaObject handleTo(LuaThread thread, Object obj) {
		return tolua(thread, obj);
	}

}
