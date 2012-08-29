package org.lua.commons.customapi.javafunctions.handlers;

import org.lua.commons.baseapi.LuaThread;
import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;
import org.lua.commons.baseapi.types.LuaNumber;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.customapi.javafunctions.handlers.types.ClassType;
import org.lua.commons.customapi.javafunctions.handlers.types.Type;

public class NumberTypeHandler extends TypeHandler {

	public NumberTypeHandler(TypeCastManager castManager) {
		super(castManager);
	}

	public Class<?>[] getClasses() {
		return new Class<?>[] { byte.class, short.class, int.class, long.class,
				float.class, double.class, Byte.class, Short.class,
				Integer.class, Long.class, Float.class, Double.class,
				Number.class };
	}

	public Object handleFrom(LuaThread thread, LuaObject obj, Type expected) {
		if (expected instanceof ClassType) {
			Class<?> clazz = ((ClassType) expected).getType();
			if (clazz.isPrimitive()) {
				if (clazz.equals(byte.class)) {
					if (obj instanceof LuaNumber)
						return ((LuaNumber) obj).toByte(thread);
				} else if (clazz.equals(short.class)) {
					if (obj instanceof LuaNumber)
						return ((LuaNumber) obj).toShort(thread);
				} else if (clazz.equals(int.class)) {
					if (obj instanceof LuaNumber)
						return ((LuaNumber) obj).toInt(thread);
				} else if (clazz.equals(long.class)) {
					if (obj instanceof LuaNumber)
						return ((LuaNumber) obj).toLong(thread);
				} else if (clazz.equals(float.class)) {
					if (obj instanceof LuaNumber)
						return ((LuaNumber) obj).toFloat(thread);
				} else if (clazz.equals(double.class)) {
					if (obj instanceof LuaNumber)
						return ((LuaNumber) obj).toDouble(thread);
				}
			} else {
				if (clazz.equals(Byte.class)) {
					if (obj instanceof LuaNumber)
						return ((LuaNumber) obj).toByte(thread);
				} else if (clazz.equals(Short.class)) {
					if (obj instanceof LuaNumber)
						return ((LuaNumber) obj).toShort(thread);
				} else if (clazz.equals(Integer.class)) {
					if (obj instanceof LuaNumber)
						return ((LuaNumber) obj).toInt(thread);
				} else if (clazz.equals(Long.class)) {
					if (obj instanceof LuaNumber)
						return ((LuaNumber) obj).toLong(thread);
				} else if (clazz.equals(Float.class)) {
					if (obj instanceof LuaNumber)
						return ((LuaNumber) obj).toFloat(thread);
				} else if (clazz.equals(Double.class)) {
					if (obj instanceof LuaNumber)
						return ((LuaNumber) obj).toDouble(thread);
				} else if (clazz.equals(Number.class)) {
					if (obj instanceof LuaNumber)
						return ((LuaNumber) obj).toDouble(thread);
				}
			}
		}
		throw new LuaTypeCastException("Can not to cast lua object "
				+ obj.getClass().getName() + " to expected java object "
				+ expected.toString());
	}

	public LuaNumber handleTo(LuaThread thread, Object obj) {
		if (obj instanceof Byte) {
			return tolua(thread, ((Byte) obj).byteValue());
		} else if (obj instanceof Short) {
			return tolua(thread, ((Short) obj).shortValue());
		} else if (obj instanceof Integer) {
			return tolua(thread, ((Integer) obj).intValue());
		} else if (obj instanceof Long) {
			return tolua(thread, ((Long) obj).longValue());
		} else if (obj instanceof Float) {
			return tolua(thread, ((Float) obj).floatValue());
		} else if (obj instanceof Double) {
			return tolua(thread, ((Double) obj).doubleValue());
		}
		throw new LuaTypeCastException("Can not to cast java object "
				+ obj.getClass().getName() + " to lua object");
	}

}
