package org.lua.commons.baseapi.functions;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.functions.Functions.Function1;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.nativeapi.LuaState;
import org.lua.commons.nativeapi.LuaStateImpl;

public class BaseJavaFunction {

	private final Lua lua;
	private final Function1<LuaThread, Integer> function;
	private LuaTable env;

	public BaseJavaFunction(Lua lua, Function1<LuaThread, Integer> function,
			LuaTable env) {
		this.lua = lua;
		this.function = function;
		this.env = env;
	}

	public LuaTable getEnv() {
		return env;
	}

	public void setEnv(LuaTable env) {
		this.env = env;
	}

	@SuppressWarnings("unused")
	private int invoke(long peer) {
		LuaState state = new EnvLuaStateImpl(peer, env);
		LuaThread thread = new LuaThread(state, lua);
		return function.invoke(thread);
	}

	private class EnvLuaStateImpl extends LuaStateImpl {

		protected final LuaTable env;

		public EnvLuaStateImpl(long peer, LuaTable env) {
			super(peer);
			this.env = env;
		}

		@Override
		public void getGlobal(String name) {
			if (env == null)
				super.getGlobal(name);
			else {
				env.push(this);
				super.pushString(name);
				super.getTable(-2);
				super.insert(-2);
				super.pop(1);
			}
		}

		@Override
		public void setGlobal(String name) {
			if (env == null)
				super.setGlobal(name);
			else {
				env.push(this);
				super.pushString(name);
				super.pushValue(-3);
				super.setTable(-3);
				super.pop(2);
			}

		}

	}

}
