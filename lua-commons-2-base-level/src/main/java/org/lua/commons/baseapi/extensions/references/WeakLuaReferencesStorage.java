package org.lua.commons.baseapi.extensions.references;

import static org.lua.commons.baseapi.LuaStack.checkStack;
import static org.lua.commons.baseapi.LuaStack.pushValue;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.nativeapi.LuaRuntimeException;
import org.lua.commons.nativeapi.LuaState;

public class WeakLuaReferencesStorage implements LuaReferencesStorage {

	private final Lua lua;
	private boolean isClosed = false;
	private final ReentrantLock lock = new ReentrantLock();
	private final LinkedList<LuaWeakBucket>[] refs;
	private int current = 0;

	@SuppressWarnings("unchecked")
	public WeakLuaReferencesStorage(Lua lua) {
		this.lua = lua;
		refs = new LinkedList[2];
		for (int i = 0; i < refs.length; i++) {
			refs[i] = new LinkedList<LuaWeakBucket>();
		}
	}

	public void start() {
		Timer gc = new Timer("weak-lua-references-service-gc-timer", true);
		gc.schedule(new GCTimer(), 10000, 20000);
	}

	public LuaReference reference(LuaThread thread, int index) {
		if (isClosed)
			throw new LuaRuntimeException("References service closed already");
		lock.lock();
		try {
			if (isClosed)
				throw new LuaRuntimeException(
						"References service closed already");
			checkStack(thread, 1);
			pushValue(thread, index);
			int ref = thread.state.ref(thread.state.getLUA_REGISTRYINDEX());
			LuaReference reference = new LuaReference(ref);
			refs[current].add(new LuaWeakBucket(ref, reference));
			return reference;
		} finally {
			lock.unlock();
		}
	}

	public void push(LuaThread thread, LuaReference reference) {
		push(thread.state, reference);
	}

	public void push(LuaState state, LuaReference reference) {
		if (isClosed)
			throw new LuaRuntimeException("References service closed already");
		state.rawGetI(state.getLUA_REGISTRYINDEX(), reference.ref);
	}

	public void close() {
		lock.lock();
		try {
			isClosed = true;
		} finally {
			lock.unlock();
		}
		for (LinkedList<LuaWeakBucket> reflist : refs)
			for (LuaWeakBucket ref : reflist)
				tryToFreeBucket(ref, true);
	}

	private void tryToFreeBucket(LuaWeakBucket bucket, boolean force) {
		if (bucket.get() == null || force) {
			LuaThread thread = lua.contextThread();
			thread.state.unref(thread.state.getLUA_REGISTRYINDEX(),
					bucket.reference);
		}
	}

	private void gc() {
		synchronized (refs) {
			lock.lock();
			if (isClosed)
				return;
			try {
				if (refs[0].size() < refs[1].size())
					current = 0;
				else
					current = 1;
			} finally {
				lock.unlock();
			}
			Iterator<LuaWeakBucket> iter = refs[1 - current].iterator();
			while (iter.hasNext()) {
				LuaWeakBucket bucket = iter.next();
				tryToFreeBucket(bucket, false);
			}
		}
	}

	private static class LuaWeakBucket extends WeakReference<Object> {
		private final int reference;

		LuaWeakBucket(int reference, Object refObject) {
			super(refObject);
			this.reference = reference;
		}
	}

	private class GCTimer extends TimerTask {
		public void run() {
			gc();
		}
	}

}
