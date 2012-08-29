package org.lua.commons.customapi.sandbox;

import org.lua.commons.baseapi.BaseLuaThread;
import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.LuaThread;

public class SandboxLuaThread extends BaseLuaThread {

	public static LuaThread safeThread(LuaThread origin, Lua safeLua,
			Class<? extends LuaStateWrapper> stateWrapperClass) {
		origin.state.checkStack(1);
		origin.state.pushThread();
		LuaThread safethread = new LuaThread(LuaStateWrapper.newInstance(
				stateWrapperClass, origin.state.toThread(-1)), safeLua);
		origin.state.pop(1);
		return safethread;
	}

	public static LuaThread newSafeThread(LuaThread thread, Lua safeLua,
			Class<? extends LuaStateWrapper> stateWrapperClass) {
		thread.state.checkStack(1);
		thread.state.newThread();
		LuaThread newthread = new LuaThread(LuaStateWrapper.newInstance(
				stateWrapperClass, thread.state.toThread(-1)), safeLua);
		thread.state.pop(1);
		return newthread;
	}

}
