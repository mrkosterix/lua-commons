package org.lua.commons.baseapi;

public class BaseLuaThread {

	public static LuaThread newThread(LuaThread thread) {
		thread.state.checkStack(1);
		thread.state.newThread();
		LuaThread newthread = new LuaThread(thread.state.toThread(-1),
				thread.lua);
		thread.state.pop(1);
		return newthread;
	}
	
}
