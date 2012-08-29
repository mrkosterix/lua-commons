package org.lua.commons.customapi.container;

import static org.lua.commons.baseapi.LuaStack.popReference;
import static org.lua.commons.baseapi.LuaStack.pushObject;
import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.extensions.metatables.LuaMetatablesService;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.customapi.javafunctions.handlers.TypeCastManager;
import org.lua.commons.customapi.javafunctions.handlers.TypeHandler;
import org.lua.commons.customapi.object.JavaObjectBuilder;
import org.lua.commons.customapi.object.annotations.LuaMember;
import org.lua.commons.customapi.object.annotations.LuaResult;

public class LuaContainer {

	protected Map<String, LuaLibraryEntry> libs = new HashMap<String, LuaLibraryEntry>();

	protected Map<String, Object> objects = new HashMap<String, Object>();

	protected List<Class<?>> classes = new ArrayList<Class<?>>();

	protected List<Class<? extends TypeHandler>> handlers = new ArrayList<Class<? extends TypeHandler>>();

	protected Map<Class<? extends TypeHandler>, List<Class<?>>> customHandlers = new HashMap<Class<? extends TypeHandler>, List<Class<?>>>();

	public LuaContainer() {
	}

	public void addLibs(String namespace, LuaLibrariesSet libSet) {
		for (LuaLibrary lib : libSet.getLibs()) {
			if (namespace == null)
				addLib(null, lib, null, null);
			else
				addLib(namespace + lib.getNamespace(), lib, null, null);
		}
	}

	public void addLib(String namespace, LuaLibrary lib, Set<String> includes,
			Set<String> excludes) {
		String ns = namespace;
		if (ns == null)
			ns = lib.getNamespace();
		libs.put(ns, new LuaLibraryEntry(lib, includes, excludes));
	}

	public void removeLib(String namespace) {
		libs.remove(namespace);
	}

	public void addObject(String namespace, Object object) {
		objects.put(namespace, object);
	}

	public void removeObject(String namespace) {
		objects.remove(namespace);
	}

	public void addClass(Class<?> clazz) {
		classes.add(clazz);
	}

	public void removeClass(Class<?> clazz) {
		classes.remove(clazz);
	}

	public void addHandler(Class<? extends TypeHandler> handler) {
		handlers.add(handler);
	}

	public void addHandler(Class<? extends TypeHandler> handler,
			Class<?>... keys) {
		List<Class<?>> keysList = customHandlers.get(handler);
		if (keysList == null) {
			keysList = new ArrayList<Class<?>>();
			customHandlers.put(handler, keysList);
		}
		for (Class<?> key : keys)
			keysList.add(key);
	}

	public void prepare(Lua lua, LuaTable table) {
		for (Entry<String, LuaLibraryEntry> lib : libs.entrySet()) {
			lib.getValue().prepare(lua, table, lib.getKey());
		}
		for (Class<?> clazz : classes) {
			prepareClass(lua, clazz);
		}
		for (Entry<String, Object> object : objects.entrySet()) {
			prepareObject(lua, table, object.getKey(), object.getValue());
		}
		for (Class<? extends TypeHandler> handler : handlers) {
			prepareHandler(lua, handler);
		}
		for (Entry<Class<? extends TypeHandler>, List<Class<?>>> entry : customHandlers
				.entrySet()) {
			prepareCustomHandler(lua, entry.getKey(), entry.getValue());
		}
	}

	protected void prepareObject(Lua lua, LuaTable env, String namespace,
			Object object) {
		LuaThread thread = lua.contextThread();
		pushObject(thread, object);
		env.set(thread, tolua(thread, namespace), popReference(thread));
	}

	protected void prepareClass(Lua lua, Class<?> clazz) {
		if (lua.getExtension(LuaMetatablesService.class).isRegistered(clazz))
			return;
		JavaObjectBuilder builder = new JavaObjectBuilder();
		for (Field field : clazz.getFields()) {
			LuaMember member = field.getAnnotation(LuaMember.class);
			if (member != null) {
				String name = member.name();
				if (name.isEmpty()) {
					name = field.getName();
				}
				builder.addField(name, field);
			}
			LuaResult result = field.getAnnotation(LuaResult.class);
			if (result != null)
				prepareClass(lua, field.getType());
		}
		for (Method method : clazz.getMethods()) {
			LuaMember member = method.getAnnotation(LuaMember.class);
			if (member != null) {
				String name = member.name();
				if (name.isEmpty()) {
					name = method.getName();
				}
				builder.addMethod(name, method);
			}
			LuaResult result = method.getAnnotation(LuaResult.class);
			if (result != null)
				prepareClass(lua, method.getReturnType());
		}
		LuaThread thread = lua.contextThread();
		lua.getExtension(LuaMetatablesService.class).register(clazz,
				builder.build(thread));
	}

	protected void prepareHandler(Lua lua, Class<? extends TypeHandler> handler) {
		lua.getExtension(TypeCastManager.class).addHandler(handler);
	}

	protected void prepareCustomHandler(Lua lua,
			Class<? extends TypeHandler> handler, List<Class<?>> keys) {
		Class<?>[] keysArr = keys.toArray(new Class<?>[keys.size()]);
		lua.getExtension(TypeCastManager.class).add(handler, keysArr);
	}
}
