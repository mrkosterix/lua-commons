package org.lua.commons.webapi.pool;

import java.util.LinkedList;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.nativeapi.LuaStateImpl;
import org.lua.commons.webapi.LuaFrame;

public abstract class AbstractLuaPool implements LuaPool {

	protected final LuaFrame frame;

	protected final int min;

	protected final LinkedList<LuaEntry> free;
	protected final LinkedList<LuaEntry> busy;

	public AbstractLuaPool(LuaFrame frame, int min) {
		this.frame = frame;
		this.min = min;
		this.free = new LinkedList<LuaEntry>();
		this.busy = new LinkedList<LuaEntry>();
	}

	public synchronized LuaEntry get() {
		while (free.size() + busy.size() < min) {
			free.add(create());
		}
		if (free.isEmpty())
			free.add(create());

		LuaEntry entry = free.pollFirst();
		busy.push(entry);
		return entry;
	}

	public synchronized void free(LuaEntry entry) {
		busy.remove(entry);
		if (free.size() + busy.size() < min + 1)
			free.addLast(entry);
	}

	protected LuaEntry create() {
		Lua lua = new Lua(new LuaStateImpl(), new SimpleLuaContextThreadPool());
		addExtensions(lua);
		lua.start();
		return new LuaEntry(lua, frame.prepare(lua));
	}

	protected abstract void addExtensions(Lua lua);

}
