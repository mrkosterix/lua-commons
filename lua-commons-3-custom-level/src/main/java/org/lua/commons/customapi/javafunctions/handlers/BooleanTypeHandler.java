package org.lua.commons.customapi.javafunctions.handlers;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.LuaBoolean;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.customapi.javafunctions.handlers.types.ClassType;
import org.lua.commons.customapi.javafunctions.handlers.types.Type;

import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;

public class BooleanTypeHandler extends TypeHandler {

	public BooleanTypeHandler(TypeCastManager castManager) {
		super(castManager);
	}

	public Class<?>[] getClasses() {
		return new Class<?>[] { boolean.class, Boolean.class };
	}

	public Object handleFrom(LuaThread thread, LuaObject obj, Type expected) {
		if (expected instanceof ClassType) {
			Class<?> clazz = ((ClassType) expected).getType();
			if (clazz.equals(boolean.class)) {
				if (obj instanceof LuaBoolean)
					return ((LuaBoolean) obj).toBoolean(thread);
			} else if (clazz.equals(Boolean.class)) {
				if (obj instanceof LuaBoolean)
					return ((LuaBoolean) obj).toBoolean(thread);
			}
		}
		throw new LuaTypeCastException("Can not to cast lua object "
				+ obj.getClass().getName() + " to expected java object "
				+ expected.toString());
	}

	public LuaBoolean handleTo(LuaThread thread, Object obj) {
		if (obj instanceof Boolean) {
			return tolua(thread, ((Boolean) obj).booleanValue());
		}
		throw new LuaTypeCastException("Can not to cast java object "
				+ obj.getClass().getName() + " to lua object");
	}

}
