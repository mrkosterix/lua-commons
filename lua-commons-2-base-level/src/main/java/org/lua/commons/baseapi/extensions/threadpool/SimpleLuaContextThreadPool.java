package org.lua.commons.baseapi.extensions.threadpool;

import org.lua.commons.baseapi.BaseLuaThread;
import org.lua.commons.baseapi.LuaThread;

public class SimpleLuaContextThreadPool implements LuaContextThreadPool {

	protected ThreadLocal<LuaThread> thread = new ThreadLocal<LuaThread>();

	public LuaThread getContextThread(LuaThread root) {
		if (thread.get() == null) {
			synchronized (root) {
				thread.set(BaseLuaThread.newThread(root));
			}
		}
		return thread.get();
	}

	public void close() {
		// do nothing
	}

	public void start() {
		// do nothing
	}
}
