package org.lua.commons.customapi.javafunctions;

import static org.lua.commons.baseapi.LuaStack.getObject;
import static org.lua.commons.baseapi.LuaStack.getReference;
import static org.lua.commons.baseapi.LuaStack.isObject;
import static org.lua.commons.baseapi.LuaStack.pushNil;
import static org.lua.commons.baseapi.LuaStack.size;
import static org.lua.commons.customapi.CustomLuaTools.castFrom;
import static org.lua.commons.customapi.CustomLuaTools.castTo;
import static org.lua.commons.customapi.javafunctions.handlers.CastUtils.totype;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.functions.Functions.Function;
import org.lua.commons.baseapi.functions.Functions.Function1;
import org.lua.commons.baseapi.types.LuaFunction;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.nativeapi.LuaRuntimeException;
import org.lua.commons.nativeapi.LuaState;

public class CustomJavaFunction {

	protected final Method method;
	protected final boolean varArgs;
	protected final boolean varRes;

	public CustomJavaFunction(Method method, boolean varArgs, boolean varRes) {
		this.method = method;
		method.setAccessible(true);
		this.varArgs = method.isVarArgs() || varArgs;
		this.varRes = varRes;
	}

	public CustomJavaFunction(Function function, boolean varArgs, boolean varRes) {
		Method[] methods = function.getClass().getMethods();
		Method c = null;
		for (Method m : methods) {
			if (m.getName().equals("invoke")) {
				c = m;
				break;
			}
		}
		if (c == null)
			throw new LuaRuntimeException("Method invoke not found!");
		this.method = c;
		method.setAccessible(true);
		this.varArgs = varArgs;
		this.varRes = varRes;
	}

	public LuaFunction function(LuaThread thread) {
		return LuaFunction.valueOf(thread, new Function1<LuaThread, Integer>() {
			public Integer invoke(LuaThread thread) {
				return proxy(thread);
			}
		});
	}

	public LuaFunction function(LuaThread thread, LuaTable env) {
		return LuaFunction.valueOf(thread, env,
				new Function1<LuaThread, Integer>() {
					public Integer invoke(LuaThread thread) {
						return proxy(thread);
					}
				});
	}

	private int proxy(LuaThread thread) {
		boolean isStatic = Modifier.isStatic(method.getModifiers());
		Type[] types = method.getGenericParameterTypes();
		Object[] args = new Object[types.length];
		Object owner = null;

		int index = 2;
		if (!isStatic) {
			if (!isObject(thread, index))
				throw new LuaRuntimeException(
						"Failed while calling function, owner must be set for non-static objects",
						null);
			owner = getObject(thread, index);
			index++;
		}
		Annotation[][] annotations = method.getParameterAnnotations();
		int argsIndex = 0;
		while (index <= size(thread)) {
			args[argsIndex] = tryInject(thread, types[argsIndex],
					annotations[argsIndex]);
			if (args[argsIndex] != null) {
				argsIndex++;
				continue;
			}
			if (varArgs && argsIndex == types.length - 1) {
				Type vartype = types[argsIndex];
				Type componentType = null;
				Class<?> componentTypeClass = null;
				if (vartype instanceof Class) {
					Class<?> clazz = (Class<?>) vartype;
					if (!clazz.isArray())
						throw new LuaRuntimeException(
								"Last argument of method with variable arguments count must be array",
								null);
					componentTypeClass = clazz.getComponentType();
					componentType = componentTypeClass;
				} else if (vartype instanceof GenericArrayType) {
					Type type = ((GenericArrayType) vartype)
							.getGenericComponentType();
					componentType = type;
					componentTypeClass = getComponentType(type);
				} else
					throw new LuaRuntimeException(
							"Last argument of method with variable arguments count must be array",
							null);
				int size = size(thread) - index + 1;
				Object varargs = Array.newInstance(componentTypeClass, size);
				args[argsIndex] = varargs;
				int i = 0;
				while (index <= size(thread)) {
					Array.set(
							varargs,
							i,
							castFrom(thread, getReference(thread, index),
									totype(componentType)));
					i++;
					index++;
				}
				argsIndex++;
				break;
			}
			args[argsIndex] = castFrom(thread, getReference(thread, index),
					totype(types[argsIndex]));
			argsIndex++;
			index++;
		}
		while (argsIndex < types.length) {
			args[argsIndex] = tryInject(thread, types[argsIndex],
					annotations[argsIndex]);
			argsIndex++;
		}
		try {
			Object result = method.invoke(owner, args);
			Class<?> resultClass = method.getReturnType();
			if (resultClass.equals(void.class))
				return 0;
			if (result == null) {
				pushNil(thread);
				return 1;
			}
			if (resultClass.isArray() && varRes) {
				int size = Array.getLength(result);
				for (int i = 0; i < size; i++)
					castTo(thread, Array.get(result, i)).push(thread);
				return size;
			}
			castTo(thread, result).push(thread);
			return 1;
		} catch (IllegalAccessException e) {
			throw new LuaRuntimeException(
					"Error while calling method in custom java function.", e);
		} catch (InvocationTargetException e) {
			throw new LuaRuntimeException(
					"Error while calling method in custom java function.", e);
		}
	}

	private Object tryInject(LuaThread thread, Type type,
			Annotation[] annotations) {
		if (annotations != null) {
			for (Annotation annotation : annotations)
				if (annotation instanceof LuaInject) {
					if (type.equals(LuaThread.class)) {
						return thread;
					} else if (type.equals(LuaState.class)) {
						return thread.state;
					} else if (type.equals(Lua.class)) {
						return thread.lua;
					}
					break;
				}
		}
		return null;
	}

	private Class<?> getComponentType(Type type) {
		if (type instanceof Class)
			return (Class<?>) type;
		if (type instanceof ParameterizedType)
			return getComponentType(((ParameterizedType) type).getRawType());
		if (type instanceof GenericArrayType)
			return getComponentType(((GenericArrayType) type)
					.getGenericComponentType());
		throw new LuaRuntimeException(
				"Error while getting component type for custom java function.",
				null);
	}

}
