package org.lua.commons.baseapi.types;

import org.lua.commons.baseapi.LuaThread;
import static org.lua.commons.baseapi.LuaStack.*;

public class LuaJavaObject extends LuaComposite {

	public LuaJavaObject(LuaThread thread, int index) {
		super(thread, index);
	}

	public static LuaJavaObject valueOf(LuaThread thread, Object value) {
		checkStack(thread, 1);
		pushObject(thread, value);
		return popObjectRef(thread);
	}

	public Object toObject() {
		return toObject(lua.contextThread());
	}

	public Object toObject(LuaThread thread) {
		checkStack(thread, 1);
		push(thread);
		return popObject(thread);
	}

}
