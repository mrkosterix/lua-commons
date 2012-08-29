package org.lua.commons.baseapi;

import org.lua.commons.baseapi.extensions.references.LuaReference;
import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.nativeapi.LuaState;

public class LuaThread {

	public final LuaState state;
	public final Lua lua;
	@SuppressWarnings("unused")
	private final LuaReference reference;

	public LuaThread(LuaState state, Lua lua) {
		this.state = lua.resolveState(state);
		this.lua = lua;
		state.pushThread();
		this.reference = lua.getExtension(LuaReferencesStorage.class)
				.reference(this, -1);
		state.pop(1);
	}
}
