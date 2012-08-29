package org.lua.commons.customapi.javafunctions.handlers;

import org.lua.commons.customapi.javafunctions.handlers.types.ArrayType;
import org.lua.commons.customapi.javafunctions.handlers.types.ClassType;
import org.lua.commons.customapi.javafunctions.handlers.types.GenericType;
import org.lua.commons.customapi.javafunctions.handlers.types.Type;
import org.lua.commons.nativeapi.LuaRuntimeException;

import java.lang.reflect.ParameterizedType;

public class CastUtils {

	private static Class<?> getArrayComponentType(Class<?> clazz) {
		while (clazz.isArray())
			clazz = clazz.getComponentType();
		return clazz;
	}

	private static int getArrayComponentDimensions(Class<?> clazz) {
		int dimensions = 0;
		while (clazz.isArray()) {
			clazz = clazz.getComponentType();
			dimensions++;
		}
		return dimensions;
	}

	public static Type totype(java.lang.reflect.Type type) {
		if (type instanceof Class) {
			return totype((Class<?>) type);
		} else if (type instanceof ParameterizedType) {
			return totype((ParameterizedType) type);
		}
		throw new LuaRuntimeException("Could not cast java type "
				+ type.toString());
	}

	public static Type totype(Class<?> clazz) {
		if (clazz.isArray())
			return new ArrayType(totype(getArrayComponentType(clazz)),
					getArrayComponentDimensions(clazz));
		else
			return new ClassType(clazz);
	}

	public static Type totype(ParameterizedType type) {
		java.lang.reflect.Type[] types = type.getActualTypeArguments();
		Type[] luatypes = new Type[types.length];
		for (int i = 0; i < types.length; i++)
			luatypes[i] = totype(types[i]);
		return new GenericType((Class<?>) type.getRawType(), luatypes);
	}

	public static Type totype(Class<?> clazz, Type generic) {
		return new GenericType(clazz, new Type[] { generic });
	}

	public static Type totype(Class<?> clazz, Class<?> generic) {
		return totype(clazz, totype(generic));
	}

	public static Type totype(Class<?> clazz, Type key, Type value) {
		return new GenericType(clazz, new Type[] { key, value });
	}

	public static Type totype(Class<?> clazz, Class<?> key, Class<?> value) {
		return totype(clazz, totype(key), totype(value));
	}

	public static Type totype(Class<?> clazz, Type key, Class<?> value) {
		return totype(clazz, key, totype(value));
	}

	public static Type totype(Class<?> clazz, Class<?> key, Type value) {
		return totype(clazz, totype(key), value);
	}

}
