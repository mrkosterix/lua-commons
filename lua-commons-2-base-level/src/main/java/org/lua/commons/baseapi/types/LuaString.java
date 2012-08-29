package org.lua.commons.baseapi.types;

import org.lua.commons.baseapi.LuaThread;
import static org.lua.commons.baseapi.LuaStack.*;

public class LuaString extends LuaObject {

	public LuaString(LuaThread thread, int index) {
		super(thread, index);
	}

	public static LuaString valueOf(LuaThread thread, String value) {
		checkStack(thread, 1);
		pushString(thread, value);
		return popStringRef(thread);
	}

	public String toString() {
		return toString(lua.contextThread());
	}

	public String toString(LuaThread thread) {
		checkStack(thread, 1);
		push(thread);
		return popString(thread);
	}
}