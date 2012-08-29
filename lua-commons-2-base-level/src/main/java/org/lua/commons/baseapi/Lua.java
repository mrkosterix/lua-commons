package org.lua.commons.baseapi;

import java.util.HashMap;

import org.lua.commons.baseapi.extensions.LuaExtension;
import org.lua.commons.baseapi.extensions.threadpool.LuaContextThreadPool;
import org.lua.commons.nativeapi.LuaRuntimeException;
import org.lua.commons.nativeapi.LuaState;

public class Lua {

	protected LuaThread root;
	protected final LuaState state;

	protected final LuaContextThreadPool contextThreadPool;

	protected boolean isExtensionsBlocked = false;
	protected final HashMap<Class<? extends LuaExtension>, LuaExtension> extensions;

	public Lua(LuaState state, LuaContextThreadPool contextThreadPool) {
		this.extensions = new HashMap<Class<? extends LuaExtension>, LuaExtension>();
		this.contextThreadPool = contextThreadPool;
		this.state = state;
	}

	public LuaThread contextThread() {
		return contextThreadPool.getContextThread(root);
	}

	public <T extends LuaExtension> T getExtension(Class<T> key) {
		return (T) extensions.get(key);
	}

	public void addExtension(Class<? extends LuaExtension> key,
			LuaExtension value) {
		if (!isExtensionsBlocked)
			extensions.put(key, value);
		else
			throw new LuaRuntimeException(
					"Extensions was blocked for this Lua object");
	}

	public LuaState resolveState(LuaState state) {
		return state;
	}

	public void start() {
		isExtensionsBlocked = true;
		this.root = new LuaThread(state, this);
		for (LuaExtension ext : extensions.values()) {
			ext.start();
		}
	}

	public void close() {
		for (LuaExtension ext : extensions.values()) {
			ext.close();
		}
		root.state.close();
	}
}
