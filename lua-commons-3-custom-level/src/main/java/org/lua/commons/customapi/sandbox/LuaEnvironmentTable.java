package org.lua.commons.customapi.sandbox;

import static org.lua.commons.baseapi.LuaStack.checkStack;
import static org.lua.commons.baseapi.LuaStack.pop;

import java.util.List;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.functions.Functions.Function3;
import org.lua.commons.baseapi.types.LuaMetatable;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaTable;

public class LuaEnvironmentTable extends LuaTable {

	protected final LuaTable read;

	public LuaEnvironmentTable(LuaTable read, LuaThread thread, int index) {
		super(thread, index);
		this.read = read;
	}

	public static LuaEnvironmentTable newTable(LuaTable read, LuaThread thread) {
		checkStack(thread, 1);
		thread.state.newTable();
		LuaEnvironmentTable table = new LuaEnvironmentTable(read, thread, -1);
		pop(thread);
		table.initMetatable(thread);

		return table;
	}

	protected void initMetatable(LuaThread thread) {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);

		metatable.setIndex(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {

					public LuaObject invoke(LuaThread thread,
							LuaObject tableobj, LuaObject key) {
						return get(thread, key);
					}
				});
		this.setMetatable(thread, metatable);
	}

	public LuaObject get(LuaThread thread, LuaObject key) {
		LuaObject result = super.get(thread, key, false);
		if (result == null)
			return read.get(thread, key);
		return result;
	}

	public void set(LuaThread thread, LuaObject key, LuaObject value) {
		super.set(thread, key, value);
	}

	public boolean containsKey(LuaThread thread, LuaObject key) {
		boolean result = super.containsKey(thread, key, false);
		if (!result)
			return read.containsKey(thread, key);
		return result;
	}

	public List<LuaObject> getKeys(LuaThread thread) {
		return super.getKeys(thread);
	}

	public List<LuaTableEntry> getEntries(LuaThread thread) {
		return super.getEntries(thread);
	}

}
