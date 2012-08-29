package org.lua.commons.customapi.sandbox;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.extensions.threadpool.LuaContextThreadPool;
import org.lua.commons.nativeapi.LuaState;

public class SafeLua extends Lua {

	protected final Class<? extends LuaStateWrapper> stateWrapper;

	public SafeLua(LuaState state, LuaContextThreadPool contextThreadPool,
			Class<? extends LuaStateWrapper> stateWrapper) {
		super(state, contextThreadPool);
		this.stateWrapper = stateWrapper;
	}

	@Override
	public LuaState resolveState(LuaState state) {
		return LuaStateWrapper.newInstance(stateWrapper, state);
	}

	@Override
	public void close() {
		// do nothing
	}

}