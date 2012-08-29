package org.lua.commons.customapi.javafunctions.handlers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.customapi.javafunctions.handlers.types.ArrayType;
import org.lua.commons.customapi.javafunctions.handlers.types.ClassType;
import org.lua.commons.customapi.javafunctions.handlers.types.Type;
import org.lua.commons.nativeapi.LuaRuntimeException;

public class SimpleTypeCastManager implements TypeCastManager {

	private final Map<Class<?>, TypeHandler> classHandlers = new HashMap<Class<?>, TypeHandler>();
	private final TypeHandler arrayHandler;
	private final TypeHandler defaultHandler;

	public SimpleTypeCastManager() {
		addHandler(NopTypeHandler.class);
		addHandler(BooleanTypeHandler.class);
		addHandler(NumberTypeHandler.class);
		addHandler(StringTypeHandler.class);
		addHandler(CollectionTypeHandler.class);
		addHandler(MapTypeHandler.class);
		arrayHandler = handlerInstance(ArrayTypeHandler.class);
		defaultHandler = handlerInstance(DefaultTypeHandler.class);
	}

	public void addHandler(Class<? extends TypeHandler> handler) {
		TypeHandler handlerImpl = handlerInstance(handler);
		for (Class<?> clazz : handlerImpl.getClasses())
			classHandlers.put(clazz, handlerImpl);
	}

	public void remove(Class<?> key) {
		classHandlers.remove(key);
	}

	public void add(Class<? extends TypeHandler> handler, Class<?>... keys) {
		TypeHandler handlerImpl = handlerInstance(handler);
		for (Class<?> key : keys)
			classHandlers.put(key, handlerImpl);
	}

	private TypeHandler handlerInstance(Class<? extends TypeHandler> handler) {
		try {
			Constructor<? extends TypeHandler> constructor = (Constructor<? extends TypeHandler>) handler
					.getConstructor(TypeCastManager.class);
			TypeHandler handlerImpl = constructor.newInstance(this);
			return handlerImpl;
		} catch (NoSuchMethodException e) {
			throw new LuaRuntimeException(
					"Could not create instance of type handler "
							+ handler.getName(), e);
		} catch (InvocationTargetException e) {
			throw new LuaRuntimeException(
					"Could not create instance of type handler "
							+ handler.getName(), e);
		} catch (InstantiationException e) {
			throw new LuaRuntimeException(
					"Could not create instance of type handler "
							+ handler.getName(), e);
		} catch (IllegalAccessException e) {
			throw new LuaRuntimeException(
					"Could not create instance of type handler "
							+ handler.getName(), e);
		}
	}

	public Object castFrom(LuaThread thread, LuaObject obj, Type expected) {
		if (expected == null) {
			return defaultHandler.handleFrom(thread, obj, expected);
		} else {
			if (expected instanceof ArrayType) {
				return arrayHandler.handleFrom(thread, obj, expected);
			} else if (expected instanceof ClassType) {
				TypeHandler handler = getClassHandlerFor(((ClassType) expected)
						.getType());
				if (handler != null)
					return handler.handleFrom(thread, obj, expected);
			}
			throw new LuaTypeCastException("Type handler not found for type "
					+ expected.toString());
		}
	}

	private TypeHandler getClassHandlerFor(Class<?> clazz) {
		if (classHandlers.containsKey(clazz))
			return classHandlers.get(clazz);
		return null;
	}

	public LuaObject castTo(LuaThread thread, Object obj) {
		Class<?> clazz = obj.getClass();
		if (clazz.isArray()) {
			return arrayHandler.handleTo(thread, obj);
		} else {
			while (true) {
				TypeHandler handler = getClassHandlerFor(clazz);
				if (handler != null)
					return handler.handleTo(thread, obj);
				if (clazz.equals(Object.class))
					break;
				clazz = clazz.getSuperclass();
			}
		}
		return defaultHandler.handleTo(thread, obj);
	}

	public void start() {
		// do nothing
	}

	public void close() {
		// do nothing
	}

}
