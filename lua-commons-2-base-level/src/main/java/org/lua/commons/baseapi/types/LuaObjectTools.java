package org.lua.commons.baseapi.types;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.functions.Functions.Function1;

public class LuaObjectTools {

	public static LuaBoolean tolua(LuaThread thread, boolean value) {
		return LuaBoolean.valueOf(thread, value);
	}

	public static LuaBoolean tolua(LuaThread thread, Boolean value) {
		if (value == null)
			return null;
		return LuaBoolean.valueOf(thread, value);
	}

	public static LuaNumber tolua(LuaThread thread, byte value) {
		return LuaNumber.valueOf(thread, value);
	}

	public static LuaNumber tolua(LuaThread thread, Byte value) {
		if (value == null)
			return null;
		return LuaNumber.valueOf(thread, value);
	}

	public static LuaNumber tolua(LuaThread thread, short value) {
		return LuaNumber.valueOf(thread, value);
	}

	public static LuaNumber tolua(LuaThread thread, Short value) {
		if (value == null)
			return null;
		return LuaNumber.valueOf(thread, value);
	}

	public static LuaNumber tolua(LuaThread thread, int value) {
		return LuaNumber.valueOf(thread, value);
	}

	public static LuaNumber tolua(LuaThread thread, Integer value) {
		if (value == null)
			return null;
		return LuaNumber.valueOf(thread, value);
	}

	public static LuaNumber tolua(LuaThread thread, long value) {
		return LuaNumber.valueOf(thread, value);
	}

	public static LuaNumber tolua(LuaThread thread, Long value) {
		if (value == null)
			return null;
		return LuaNumber.valueOf(thread, value);
	}

	public static LuaNumber tolua(LuaThread thread, float value) {
		return LuaNumber.valueOf(thread, value);
	}

	public static LuaNumber tolua(LuaThread thread, Float value) {
		if (value == null)
			return null;
		return LuaNumber.valueOf(thread, value);
	}

	public static LuaNumber tolua(LuaThread thread, double value) {
		return LuaNumber.valueOf(thread, value);
	}

	public static LuaNumber tolua(LuaThread thread, Double value) {
		if (value == null)
			return null;
		return LuaNumber.valueOf(thread, value);
	}

	public static LuaString tolua(LuaThread thread, String value) {
		if (value == null)
			return null;
		return LuaString.valueOf(thread, value);
	}

	public static LuaFunction tolua(LuaThread thread,
			Function1<LuaThread, Integer> value) {
		if (value == null)
			return null;
		return LuaFunction.valueOf(thread, value);
	}

	public static LuaJavaObject tolua(LuaThread thread, Object value) {
		if (value == null)
			return null;
		return LuaJavaObject.valueOf(thread, value);
	}

	public static LuaObject tolua(LuaThread thread, LuaObject value) {
		if (value == null)
			return null;
		return value;
	}

	public static Object fromlua(LuaObject value) {
		if (value == null)
			return null;
		return fromlua(value.lua.contextThread(), value);
	}

	public static Object fromlua(LuaThread thread, LuaObject value) {
		if (value == null)
			return null;
		if (value instanceof LuaBoolean)
			return ((LuaBoolean) value).toBoolean(thread);
		if (value instanceof LuaNumber)
			return ((LuaNumber) value).toDouble(thread);
		if (value instanceof LuaString)
			return ((LuaString) value).toString(thread);
		if (value instanceof LuaJavaObject)
			return ((LuaJavaObject) value).toObject(thread);
		return value;
	}

}
