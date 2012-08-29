package org.lua.commons.baseapi.types;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.extensions.references.LuaReference;
import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.nativeapi.LuaState;

public class LuaObject {

	protected Lua lua;
	private LuaReference reference;
	private LuaReferencesStorage luaReferencesStorage;

	public LuaObject(LuaThread thread, int index) {
		this.lua = thread.lua;
		this.luaReferencesStorage = lua
				.getExtension(LuaReferencesStorage.class);
		this.reference = luaReferencesStorage.reference(thread, index);
	}

	public void push(LuaThread thread) {
		luaReferencesStorage.push(thread, reference);
	}
	
	public void push(LuaState state) {
		luaReferencesStorage.push(state, reference);
	}

}
