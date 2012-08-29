package org.lua.commons.customapi.javafunctions.handlers;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.LuaBoolean;
import org.lua.commons.baseapi.types.LuaNumber;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaString;
import org.lua.commons.customapi.javafunctions.handlers.types.ClassType;
import org.lua.commons.customapi.javafunctions.handlers.types.Type;
import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;

public class StringTypeHandler extends TypeHandler {

	public StringTypeHandler(TypeCastManager castManager) {
		super(castManager);
	}

	public Class<?>[] getClasses() {
		return new Class<?>[] { String.class };
	}

	public Object handleFrom(LuaThread thread, LuaObject obj, Type expected) {
		if (expected instanceof ClassType) {
			Class<?> clazz = ((ClassType) expected).getType();
			if (clazz.equals(String.class)) {
				if (obj instanceof LuaString)
					return ((LuaString) obj).toString(thread);
				else if (obj instanceof LuaNumber)
					return String.valueOf(((LuaNumber) obj).toDouble(thread));
				else if (obj instanceof LuaBoolean)
					return String.valueOf(((LuaBoolean) obj).toBoolean(thread));
			}
		}
		throw new LuaTypeCastException("Can not to cast lua object "
				+ obj.getClass().getName() + " to java object "
				+ expected.toString());
	}

	public LuaString handleTo(LuaThread thread, Object obj) {
		if (obj instanceof String) {
			return tolua(thread, (String) obj);
		}
		throw new LuaTypeCastException("Can not to cast java object "
				+ obj.getClass().getName() + " to lua object");
	}

}
