package org.lua.commons.configuration.basic;

import static org.lua.commons.baseapi.LuaStack.popTable;
import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.customapi.container.LuaLibrary;

public abstract class StandartConfigurationApi implements LuaLibrary {

	public String getNamespace() {
		return getDefault();
	}

	public void prepare(LuaThread thread) {
		thread.state.getGlobal(getDefault());
		LuaTable table = popTable(thread);
		for (String name : getNames()) {
			table.get(thread, tolua(thread, name)).push(thread);
			thread.state.setGlobal(name);
		}
	}

	public abstract String getDefault();

	public abstract String[] getNames();

}
