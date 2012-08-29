package org.lua.commons.customapi.sandbox;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.lua.commons.nativeapi.LuaRuntimeException;
import org.lua.commons.nativeapi.LuaState;

public abstract class LuaStateWrapper implements LuaState {

	protected final LuaState root;

	public LuaStateWrapper(LuaState root) {
		this.root = root;
	}

	public static LuaStateWrapper newInstance(
			Class<? extends LuaStateWrapper> wrapper, LuaState origin) {
		try {
			Constructor<? extends LuaStateWrapper> constructor = wrapper
					.getConstructor(LuaState.class);
			return constructor.newInstance(origin);
		} catch (IllegalArgumentException e) {
			throw new LuaRuntimeException(
					"Error while creating instance of LuaStateWrapper's implementation "
							+ wrapper.toString(), e);
		} catch (InstantiationException e) {
			throw new LuaRuntimeException(
					"Error while creating instance of LuaStateWrapper's implementation "
							+ wrapper.toString(), e);
		} catch (IllegalAccessException e) {
			throw new LuaRuntimeException(
					"Error while creating instance of LuaStateWrapper's implementation "
							+ wrapper.toString(), e);
		} catch (InvocationTargetException e) {
			throw new LuaRuntimeException(
					"Error while creating instance of LuaStateWrapper's implementation "
							+ wrapper.toString(), e);
		} catch (SecurityException e) {
			throw new LuaRuntimeException(
					"Error while creating instance of LuaStateWrapper's implementation "
							+ wrapper.toString(), e);
		} catch (NoSuchMethodException e) {
			throw new LuaRuntimeException(
					"Error while creating instance of LuaStateWrapper's implementation "
							+ wrapper.toString(), e);
		}
	}

}
