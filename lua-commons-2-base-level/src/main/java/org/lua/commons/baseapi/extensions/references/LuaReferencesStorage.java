package org.lua.commons.baseapi.extensions.references;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.extensions.LuaExtension;
import org.lua.commons.nativeapi.LuaState;

public interface LuaReferencesStorage extends LuaExtension {

	public LuaReference reference(LuaThread thread, int index);

	public void push(LuaThread thread, LuaReference reference);
	
	public void push(LuaState state, LuaReference reference);

}
