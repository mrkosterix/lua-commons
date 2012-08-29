package org.lua.commons.baseapi;

import org.lua.commons.baseapi.extensions.metatables.LuaMetatablesService;
import org.lua.commons.baseapi.functions.BaseJavaFunction;
import org.lua.commons.baseapi.functions.Functions.Function1;
import org.lua.commons.baseapi.types.LuaBoolean;
import org.lua.commons.baseapi.types.LuaFunction;
import org.lua.commons.baseapi.types.LuaJavaObject;
import org.lua.commons.baseapi.types.LuaNumber;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaString;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.nativeapi.LuaRuntimeException;
import org.lua.commons.nativeapi.LuaState;

public class LuaStack {

	public static boolean isNone(LuaThread thread, int index) {
		return thread.state.isNone(index);
	}

	public static boolean isTopNone(LuaThread thread) {
		return isNone(thread, -1);
	}

	public static boolean isNil(LuaThread thread, int index) {
		return thread.state.isNil(index);
	}

	public static boolean isTopNil(LuaThread thread) {
		return isNil(thread, -1);
	}

	public static boolean isNoneOrNil(LuaThread thread, int index) {
		return thread.state.isNoneOrNil(index);
	}

	public static boolean isTopNoneOrNil(LuaThread thread) {
		return isNoneOrNil(thread, -1);
	}

	public static void pushNil(LuaThread thread) {
		thread.state.pushNil();
	}

	public static int size(LuaThread thread) {
		return thread.state.getTop();
	}

	public static boolean getBoolean(LuaThread thread, int index) {
		return thread.state.toBoolean(index);
	}

	public static LuaBoolean getBooleanRef(LuaThread thread, int index) {
		return new LuaBoolean(thread, index);
	}

	public static boolean isBoolean(LuaThread thread, int index) {
		return thread.state.isBoolean(index);
	}

	public static boolean isTopBoolean(LuaThread thread) {
		return thread.state.isBoolean(-1);
	}

	public static void pushBoolean(LuaThread thread, boolean value) {
		thread.state.pushBoolean(value);
	}

	public static boolean popBoolean(LuaThread thread) {
		boolean result = getBoolean(thread, -1);
		pop(thread);
		return result;
	}

	public static LuaBoolean popBooleanRef(LuaThread thread) {
		LuaBoolean result = getBooleanRef(thread, -1);
		pop(thread);
		return result;
	}

	public static byte getByte(LuaThread thread, int index) {
		return (byte) thread.state.toInteger(index);
	}

	public static LuaNumber getByteRef(LuaThread thread, int index) {
		return new LuaNumber(thread, index);
	}

	public static boolean isByte(LuaThread thread, int index) {
		return thread.state.isInteger(index, Byte.MIN_VALUE, Byte.MAX_VALUE);
	}

	public static boolean isTopByte(LuaThread thread) {
		return isByte(thread, -1);
	}

	public static void pushByte(LuaThread thread, byte value) {
		thread.state.pushNumber(value);
	}

	public static byte popByte(LuaThread thread) {
		byte result = getByte(thread, -1);
		pop(thread);
		return result;
	}

	public static LuaNumber popByteRef(LuaThread thread) {
		LuaNumber result = getByteRef(thread, -1);
		pop(thread);
		return result;
	}

	public static short getShort(LuaThread thread, int index) {
		return (short) thread.state.toInteger(index);
	}

	public static LuaNumber getShortRef(LuaThread thread, int index) {
		return new LuaNumber(thread, index);
	}

	public static boolean isShort(LuaThread thread, int index) {
		return thread.state.isInteger(index, Short.MIN_VALUE, Short.MAX_VALUE);
	}

	public static boolean isTopShort(LuaThread thread) {
		return isShort(thread, -1);
	}

	public static void pushShort(LuaThread thread, short value) {
		thread.state.pushNumber(value);
	}

	public static short popShort(LuaThread thread) {
		short result = getShort(thread, -1);
		pop(thread);
		return result;
	}

	public static LuaNumber popShortRef(LuaThread thread) {
		LuaNumber result = getShortRef(thread, -1);
		pop(thread);
		return result;
	}

	public static int getInt(LuaThread thread, int index) {
		return thread.state.toInteger(index);
	}

	public static LuaNumber getIntRef(LuaThread thread, int index) {
		return new LuaNumber(thread, index);
	}

	public static boolean isInt(LuaThread thread, int index) {
		return thread.state.isInteger(index, Integer.MIN_VALUE,
				Integer.MAX_VALUE);
	}

	public static boolean isTopInt(LuaThread thread) {
		return isInt(thread, -1);
	}

	public static void pushInt(LuaThread thread, int value) {
		thread.state.pushNumber(value);
	}

	public static int popInt(LuaThread thread) {
		int result = getInt(thread, -1);
		pop(thread);
		return result;
	}

	public static LuaNumber popIntRef(LuaThread thread) {
		LuaNumber result = getIntRef(thread, -1);
		pop(thread);
		return result;
	}

	public static long getLong(LuaThread thread, int index) {
		LuaState state = thread.state;
		if (state.isInteger(index, Integer.MIN_VALUE, Integer.MAX_VALUE))
			return state.toInteger(index);
		String value = state.toString(index);
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			throw new LuaRuntimeException(
					"Couldn't cast string \"$value\" to long value", e);
		}
	}

	public static LuaNumber getLongRef(LuaThread thread, int index) {
		return new LuaNumber(thread, index);
	}

	public static boolean isLong(LuaThread thread, int index) {
		LuaState state = thread.state;
		if (state.isInteger(index, Integer.MIN_VALUE, Integer.MAX_VALUE))
			return true;
		String value = state.toString(index);
		try {
			Long.parseLong(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isTopLong(LuaThread thread) {
		return isLong(thread, -1);
	}

	public static void pushLong(LuaThread thread, long value) {
		if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE)
			thread.state.pushString(String.valueOf(value));
		else
			thread.state.pushInteger((int) value);
	}

	public static long popLong(LuaThread thread) {
		long result = getLong(thread, -1);
		pop(thread);
		return result;
	}

	public static LuaNumber popLongRef(LuaThread thread) {
		LuaNumber result = getLongRef(thread, -1);
		pop(thread);
		return result;
	}

	public static float getFloat(LuaThread thread, int index) {
		return (float) thread.state.toNumber(index);
	}

	public static LuaNumber getFloatRef(LuaThread thread, int index) {
		return new LuaNumber(thread, index);
	}

	public static boolean isFloat(LuaThread thread, int index) {
		return thread.state.isNumber(index);
	}

	public static boolean isTopFloat(LuaThread thread) {
		return isFloat(thread, -1);
	}

	public static void pushFloat(LuaThread thread, float value) {
		thread.state.pushNumber(value);
	}

	public static float popFloat(LuaThread thread) {
		float result = getFloat(thread, -1);
		pop(thread);
		return result;
	}

	public static LuaNumber popFloatRef(LuaThread thread) {
		LuaNumber result = getFloatRef(thread, -1);
		pop(thread);
		return result;
	}

	public static double getDouble(LuaThread thread, int index) {
		return thread.state.toNumber(index);
	}

	public static LuaNumber getDoubleRef(LuaThread thread, int index) {
		return new LuaNumber(thread, index);
	}

	public static boolean isDouble(LuaThread thread, int index) {
		return thread.state.isNumber(index);
	}

	public static boolean isTopDouble(LuaThread thread) {
		return isDouble(thread, -1);
	}

	public static void pushDouble(LuaThread thread, double value) {
		thread.state.pushNumber(value);
	}

	public static double popDouble(LuaThread thread) {
		double result = getDouble(thread, -1);
		pop(thread);
		return result;
	}

	public static LuaNumber popDoubleRef(LuaThread thread) {
		LuaNumber result = getDoubleRef(thread, -1);
		pop(thread);
		return result;
	}

	public static String getString(LuaThread thread, int index) {
		return thread.state.toString(index);
	}

	public static LuaString getStringRef(LuaThread thread, int index) {
		return new LuaString(thread, index);
	}

	public static boolean isString(LuaThread thread, int index) {
		return thread.state.isString(index);
	}

	public static boolean isTopString(LuaThread thread) {
		return isString(thread, -1);
	}

	public static void pushString(LuaThread thread, String value) {
		thread.state.pushString(value);
	}

	public static String popString(LuaThread thread) {
		String result = getString(thread, -1);
		pop(thread);
		return result;
	}

	public static LuaString popStringRef(LuaThread thread) {
		LuaString result = getStringRef(thread, -1);
		pop(thread);
		return result;
	}

	public static Object getObject(LuaThread thread, int index) {
		return thread.state.toObject(index);
	}

	public static LuaJavaObject getObjectRef(LuaThread thread, int index) {
		return new LuaJavaObject(thread, index);
	}

	public static boolean isObject(LuaThread thread, int index) {
		return thread.state.isObject(index);
	}

	public static boolean isTopObject(LuaThread thread) {
		return isObject(thread, -1);
	}

	public static void pushObject(LuaThread thread, Object value) {
		thread.state.pushObject(value);
		LuaMetatablesService service = thread.lua
				.getExtension(LuaMetatablesService.class);
		if (service != null) {
			getObjectRef(thread, -1).setMetatable(thread, service.get(value));
		}
	}

	public static Object popObject(LuaThread thread) {
		Object result = getObject(thread, -1);
		pop(thread);
		return result;
	}

	public static LuaJavaObject popObjectRef(LuaThread thread) {
		LuaJavaObject result = getObjectRef(thread, -1);
		pop(thread);
		return result;
	}

	public static LuaFunction getFunction(LuaThread thread, int index) {
		return new LuaFunction(thread, index);
	}

	public static boolean isFunction(LuaThread thread, int index) {
		return thread.state.isFunction(index);
	}

	public static boolean isTopFunction(LuaThread thread) {
		return isFunction(thread, -1);
	}

	public static LuaFunction popFunction(LuaThread thread) {
		LuaFunction result = getFunction(thread, -1);
		pop(thread);
		return result;
	}

	public static LuaTable getTable(LuaThread thread, int index) {
		return new LuaTable(thread, index);
	}

	public static boolean isTable(LuaThread thread, int index) {
		return thread.state.isTable(index);
	}

	public static boolean isTopTable(LuaThread thread) {
		return isTable(thread, -1);
	}

	public static LuaTable popTable(LuaThread thread) {
		LuaTable result = getTable(thread, -1);
		pop(thread);
		return result;
	}

	public static void pushJavaClosure(LuaThread thread, LuaTable env,
			LuaObject[] args, Function1<LuaThread, Integer> function) {
		BaseJavaFunction funObject = new BaseJavaFunction(thread.lua, function,
				env);
		if (env == null) {
			pushNil(thread);
		} else {
			env.push(thread);
		}
		for (LuaObject arg : args)
			arg.push(thread);
		thread.state.pushJClosure(funObject, "invoke", args.length + 1);
	}

	public static void pushJavaClosure(LuaThread thread, LuaObject[] args,
			Function1<LuaThread, Integer> function) {
		pushJavaClosure(thread, null, args, function);
	}

	public static void pushJavaFunction(LuaThread thread, LuaTable env,
			Function1<LuaThread, Integer> function) {
		pushJavaClosure(thread, env, new LuaObject[0], function);
	}

	public static void pushJavaFunction(LuaThread thread,
			Function1<LuaThread, Integer> function) {
		pushJavaFunction(thread, null, function);
	}

	public static void pop(LuaThread thread) {
		pop(thread, 1);
	}

	public static void pop(LuaThread thread, int count) {
		thread.state.pop(count);
	}

	public static void popAll(LuaThread thread) {
		pop(thread, size(thread));
	}

	public static void pushValue(LuaThread thread, int to) {
		thread.state.pushValue(to);
	}

	public static void checkStack(LuaThread thread, int extra, String msg) {
		thread.state.LcheckStack(extra, msg);
	}

	public static void checkStack(LuaThread thread, int extra) {
		checkStack(thread, extra, "Could not increase lua stack size.");
	}

	public static LuaObject getReference(LuaThread thread, int index) {
		if (isNil(thread, index))
			return null;
		if (isBoolean(thread, index))
			return new LuaBoolean(thread, index);
		if (isDouble(thread, index))
			return new LuaNumber(thread, index);
		if (isString(thread, index))
			return new LuaString(thread, index);
		if (isFunction(thread, index))
			return new LuaFunction(thread, index);
		if (isTable(thread, index))
			return new LuaTable(thread, index);
		if (isObject(thread, index))
			return new LuaJavaObject(thread, index);
		return new LuaObject(thread, index);
	}

	public static LuaObject popReference(LuaThread thread) {
		LuaObject result = getReference(thread, -1);
		pop(thread);
		return result;
	}

	public static void pushReference(LuaThread thread, LuaObject reference) {
		reference.push(thread);
	}

}
