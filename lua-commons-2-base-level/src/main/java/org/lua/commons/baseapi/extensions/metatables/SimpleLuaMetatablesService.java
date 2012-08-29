package org.lua.commons.baseapi.extensions.metatables;

import java.util.HashMap;
import java.util.Map;

import org.lua.commons.baseapi.types.LuaMetatable;

public class SimpleLuaMetatablesService implements LuaMetatablesService {

	private Map<Class<?>, LuaMetatable> metatables = new HashMap<Class<?>, LuaMetatable>();

	public void register(Class<?> key, LuaMetatable metatable) {
		metatables.put(key, metatable);
	}

	public void unregister(Class<?> key) {
		metatables.remove(key);
	}

	public LuaMetatable get(Object obj) {
		Class<?> clazz = obj.getClass();
		while (clazz.getSuperclass() != null) {
			LuaMetatable metatable = metatables.get(clazz);
			if (metatable != null)
				return metatable;
			clazz = clazz.getSuperclass();
		}
		return null;
	}

	public void close() {
		// do nothing
	}

	public void start() {
		// do nothing
	}

	public boolean isRegistered(Class<?> key) {
		return metatables.containsKey(key);
	}

}
