package org.lua.commons.baseapi.types;

import static org.lua.commons.baseapi.LuaStack.checkStack;
import static org.lua.commons.baseapi.LuaStack.getReference;
import static org.lua.commons.baseapi.LuaStack.isTopNil;
import static org.lua.commons.baseapi.LuaStack.pop;
import static org.lua.commons.baseapi.LuaStack.popReference;
import static org.lua.commons.baseapi.LuaStack.pushNil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.lua.commons.baseapi.LuaThread;

public class LuaTable extends LuaComposite {

	public LuaTable(LuaThread thread, int index) {
		super(thread, index);
	}

	public static LuaTable newTable(LuaThread thread) {
		checkStack(thread, 1);
		thread.state.newTable();
		LuaTable table = new LuaTable(thread, -1);
		pop(thread);
		return table;
	}

	protected LuaObject get(LuaThread thread, LuaObject key,
			boolean withMetatable) {
		checkStack(thread, 2);
		push(thread);
		key.push(thread);
		if (!withMetatable)
			thread.state.rawGet(-2);
		else
			thread.state.getTable(-2);
		if (isTopNil(thread)) {
			pop(thread, 2);
			return null;
		}
		LuaObject result = popReference(thread);
		pop(thread);
		return result;
	}

	public LuaObject get(LuaThread thread, LuaObject key) {
		return get(thread, key, true);
	}

	protected void set(LuaThread thread, LuaObject key, LuaObject value,
			boolean withMetatable) {
		checkStack(thread, 3);
		push(thread);
		key.push(thread);
		if (value != null)
			value.push(thread);
		else
			pushNil(thread);
		if (!withMetatable)
			thread.state.rawSet(-3);
		else
			thread.state.setTable(-3);
		pop(thread);
	}

	public void set(LuaThread thread, LuaObject key, LuaObject value) {
		set(thread, key, value, true);
	}

	protected boolean containsKey(LuaThread thread, LuaObject key,
			boolean withMetatable) {
		checkStack(thread, 2);
		push(thread);
		key.push(thread);
		if (!withMetatable)
			thread.state.rawGet(-2);
		else
			thread.state.getTable(-2);
		boolean result = !isTopNil(thread);
		pop(thread, 2);
		return result;
	}

	public boolean containsKey(LuaThread thread, LuaObject key) {
		return containsKey(thread, key, true);
	}

	public List<LuaObject> getKeys(LuaThread thread) {
		checkStack(thread, 3);
		List<LuaObject> keys = new ArrayList<LuaObject>();
		push(thread);
		pushNil(thread);
		while (thread.state.next(-2)) {
			keys.add(getReference(thread, -2));
			pop(thread);
		}
		pop(thread);
		return keys;
	}

	public List<LuaTableEntry> getEntries(LuaThread thread) {
		checkStack(thread, 3);
		List<LuaTableEntry> entries = new ArrayList<LuaTableEntry>();
		push(thread);
		pushNil(thread);
		while (thread.state.next(-2)) {
			entries.add(new LuaTableEntry(getReference(thread, -2),
					getReference(thread, -1)));
			pop(thread);
		}
		pop(thread);
		return entries;
	}

	public static class LuaTableEntry implements Entry<LuaObject, LuaObject> {

		private final LuaObject key;
		private final LuaObject value;

		public LuaTableEntry(LuaObject key, LuaObject value) {
			this.key = key;
			this.value = value;
		}

		public LuaObject getKey() {
			return key;
		}

		public LuaObject getValue() {
			return value;
		}

		public LuaObject setValue(LuaObject value) {
			throw new UnsupportedOperationException();
		}
	}
}
