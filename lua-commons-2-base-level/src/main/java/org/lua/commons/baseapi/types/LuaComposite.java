package org.lua.commons.baseapi.types;

import static org.lua.commons.baseapi.LuaStack.checkStack;
import static org.lua.commons.baseapi.LuaStack.pop;
import static org.lua.commons.baseapi.LuaStack.pushNil;

import org.lua.commons.baseapi.LuaThread;

public class LuaComposite extends LuaObject {

	public LuaComposite(LuaThread thread, int index) {
		super(thread, index);
	}

	public LuaMetatable getMetatable(LuaThread thread) {
		checkStack(thread, 2);
		push(thread);
		if (!thread.state.getMetatable(-1)) {
			pop(thread);
			return null;
		}
		LuaMetatable result = new LuaMetatable(thread, -1);
		pop(thread, 2);
		return result;
	}

	public void setMetatable(LuaThread thread, LuaMetatable metatable) {
		checkStack(thread, 2);
		push(thread);
		if (metatable == null)
			pushNil(thread);
		else
			metatable.push(thread);
		thread.state.setMetatable(-2);
		pop(thread);
	}

}
