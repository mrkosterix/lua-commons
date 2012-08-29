package org.lua.commons.baseapi.types;

import static org.lua.commons.baseapi.LuaStack.checkStack;
import static org.lua.commons.baseapi.LuaStack.getReference;
import static org.lua.commons.baseapi.LuaStack.isTopTable;
import static org.lua.commons.baseapi.LuaStack.pop;
import static org.lua.commons.baseapi.LuaStack.popFunction;
import static org.lua.commons.baseapi.LuaStack.popObject;
import static org.lua.commons.baseapi.LuaStack.popReference;
import static org.lua.commons.baseapi.LuaStack.popString;
import static org.lua.commons.baseapi.LuaStack.popTable;
import static org.lua.commons.baseapi.LuaStack.pushJavaFunction;
import static org.lua.commons.baseapi.LuaStack.pushNil;
import static org.lua.commons.baseapi.LuaStack.size;

import java.util.ArrayList;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.functions.BaseJavaFunction;
import org.lua.commons.baseapi.functions.Functions.Function1;
import org.lua.commons.nativeapi.LuaException;

public class LuaFunction extends LuaObject {

	protected final String _ENV = "_ENV";

	public LuaFunction(LuaThread thread, int index) {
		super(thread, index);
	}

	public static LuaFunction valueOf(LuaThread thread,
			Function1<LuaThread, Integer> function) {
		checkStack(thread, 1);
		pushJavaFunction(thread, function);
		return popFunction(thread);
	}

	public static LuaFunction valueOf(LuaThread thread, LuaTable env,
			Function1<LuaThread, Integer> function) {
		checkStack(thread, 1);
		pushJavaFunction(thread, env, function);
		return popFunction(thread);
	}

	public boolean isNeedEnv(LuaThread thread) {
		checkStack(thread, 2);
		push(thread);
		if (thread.state.isJFunction(-1)) {
			return true;
		} else {
			if (_ENV.equals(thread.state.getUpValue(-1, 1))
					&& isTopTable(thread)) {
				pop(thread, 2);
				return true;
			}
			pop(thread, 2);
			return false;
		}
	}

	public LuaTable getEnv(LuaThread thread) {
		checkStack(thread, 2);
		push(thread);
		if (thread.state.isJFunction(-1)) {
			thread.state.getUpValue(-1, 1);
			BaseJavaFunction funcobj = (BaseJavaFunction) popObject(thread);
			pop(thread);
			return funcobj.getEnv();
		} else {
			if (_ENV.equals(thread.state.getUpValue(-1, 1))
					&& isTopTable(thread)) {
				LuaTable table = popTable(thread);
				pop(thread);
				return table;
			}
			pop(thread, 2);
			return null;
		}
	}

	public void setEnv(LuaThread thread, LuaTable env) {
		checkStack(thread, 3);
		push(thread);
		if (thread.state.isJFunction(-1)) {
			thread.state.getUpValue(-1, 1);
			BaseJavaFunction funcobj = (BaseJavaFunction) popObject(thread);
			funcobj.setEnv(env);
			pop(thread);
			return;
		} else {
			if (_ENV.equals(thread.state.getUpValue(-1, 1))) {
				pop(thread);
				env.push(thread);
				thread.state.setUpValue(-2, 1);
				pop(thread);
				return;
			}
			pop(thread, 2);
			return;
		}
	}

	public String[] getUpvalueNames(LuaThread thread) {
		checkStack(thread, 2);
		ArrayList<String> upvalues = new ArrayList<String>();
		push(thread);
		String curr = thread.state.getUpValue(-1, 1);
		int i = 2;
		while (curr != null) {
			if (!curr.equals(_ENV))
				upvalues.add(curr);
			pop(thread);
			curr = thread.state.getUpValue(-1, i);
			i++;
		}
		pop(thread);
		return upvalues.toArray(new String[upvalues.size()]);
	}

	public LuaObject getUpvalue(LuaThread thread, int index) {
		checkStack(thread, 2);
		push(thread);
		thread.state.getUpValue(-1, index);
		LuaObject result = popReference(thread);
		pop(thread);
		return result;
	}

	public void setUpvalue(LuaThread thread, int index, LuaObject value) {
		checkStack(thread, 2);
		push(thread);
		if (value == null)
			pushNil(thread);
		value.push(thread);
		int idx = index;
		if (isNeedEnv(thread))
			idx++;
		thread.state.setUpValue(-2, idx);
		pop(thread);
	}

	public LuaObject[] call(LuaThread thread, LuaObject[] args, int nRes)
			throws LuaException {
		checkStack(thread, 1 + args.length);
		int stackSize = size(thread);
		push(thread);
		for (LuaObject arg : args) {
			arg.push(thread);
		}
		int code = thread.state.pcall(args.length, nRes, 0);
		if (code != 0) {
			String message = popString(thread);
			throw new LuaException("Lua function throws error with status "
					+ code + " and message " + message);
		}
		int index = stackSize + 1;
		LuaObject[] results = new LuaObject[size(thread) - stackSize];
		for (int i = 0; i < results.length; i++) {
			results[i] = getReference(thread, index + i);
		}
		pop(thread, size(thread) - stackSize);
		return results;
	}
}