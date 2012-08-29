package org.lua.commons.customapi;

import org.lua.commons.baseapi.BaseLuaTools;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.customapi.javafunctions.handlers.TypeCastManager;
import org.lua.commons.customapi.javafunctions.handlers.types.Type;

public class CustomLuaTools extends BaseLuaTools {

	public static Object castFrom(LuaThread thread, LuaObject object,
			Type expected) {
		return thread.lua.getExtension(TypeCastManager.class).castFrom(thread,
				object, expected);
	}

	public static LuaObject castTo(LuaThread thread, Object object) {
		return thread.lua.getExtension(TypeCastManager.class).castTo(thread,
				object);
	}

}