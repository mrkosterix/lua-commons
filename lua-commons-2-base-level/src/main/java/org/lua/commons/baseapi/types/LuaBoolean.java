package org.lua.commons.baseapi.types;

import org.lua.commons.baseapi.LuaThread;
import static org.lua.commons.baseapi.LuaStack.*;

public class LuaBoolean extends LuaObject {

	public LuaBoolean(LuaThread thread, int index) {
		super(thread, index);
	}

	public static LuaBoolean valueOf(LuaThread thread, boolean value) {
		checkStack(thread, 1);
		pushBoolean(thread, value);
		return popBooleanRef(thread);
	}

	public boolean toBoolean() {
		return toBoolean(lua.contextThread());
	}

	public boolean toBoolean(LuaThread thread) {
		checkStack(thread, 1);
		push(thread);
		return popBoolean(thread);
	}

}
